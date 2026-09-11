package com.ruoyi.web.controller.threebase;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 体系三基融合工作平台通用业务接口。
 *
 * 当前阶段先承载招标文件和功能清单要求的模块、功能、台账、流程动作等通用能力，
 * 后续可以将内存仓储替换为 MyBatis 持久化服务。
 */
@RestController
@RequestMapping("/system/threebase")
public class ThreebaseController
{
    private static final AtomicLong ID = new AtomicLong(1000);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<Map<String, Object>> MODULES = loadModules();
    private static final Map<String, List<Map<String, Object>>> RECORDS = new ConcurrentHashMap<>();

    @GetMapping("/modules")
    public AjaxResult modules()
    {
        return AjaxResult.success(Collections.singletonMap("modules", MODULES));
    }

    @GetMapping("/modules/{moduleCode}/features")
    public AjaxResult features(@PathVariable String moduleCode)
    {
        Map<String, Object> module = findModule(moduleCode);
        List<Map<String, Object>> features = module == null ? Collections.emptyList() : listValue(module.get("features"));
        return AjaxResult.success(Collections.singletonMap("features", features));
    }

    @GetMapping("/dashboard")
    public AjaxResult dashboard()
    {
        int featureCount = MODULES.stream().mapToInt(module -> listValue(module.get("features")).size()).sum();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("moduleCount", MODULES.size());
        data.put("featureCount", featureCount);
        data.put("integrationCount", integrationsData().size());
        data.put("recordCount", RECORDS.values().stream().mapToInt(List::size).sum());
        data.put("todoCount", Math.max(8, MODULES.size()));
        data.put("sla", "A 级系统 SLA，7x24 监控，可用率不低于 99.9%");
        return AjaxResult.success(data);
    }

    @GetMapping("/integrations")
    public AjaxResult integrations()
    {
        return AjaxResult.success(Collections.singletonMap("integrations", integrationsData()));
    }

    @GetMapping("/todos")
    public AjaxResult todos()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(todo("流程待确认", "BPM 流程节点、审核角色、归档规则待确认", "本阶段", "warning"));
        rows.add(todo("统一身份联调", "用户、部门、岗位角色同步接口待联调", "本阶段", "primary"));
        rows.add(todo("数据初始化", "制度、标准、台账和考评指标基础数据待整理导入", "下一阶段", "success"));
        rows.add(todo("信创部署准备", "部署环境、数据库、中间件和上线切换方案待确认", "下一阶段", "info"));
        return AjaxResult.success(Collections.singletonMap("rows", rows));
    }

    @GetMapping("/records")
    public AjaxResult records(@RequestParam String moduleCode,
                              @RequestParam(required = false) String featureCode,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String status,
                              @RequestParam(defaultValue = "1") int pageNum,
                              @RequestParam(defaultValue = "10") int pageSize)
    {
        ensureSeed(moduleCode, featureCode);
        List<Map<String, Object>> rows = new ArrayList<>();
        if (featureCode != null && !featureCode.isBlank())
        {
            rows.addAll(RECORDS.getOrDefault(recordKey(moduleCode, featureCode), Collections.emptyList()));
        }
        else
        {
            Map<String, Object> module = findModule(moduleCode);
            if (module != null)
            {
                for (Map<String, Object> feature : listValue(module.get("features")))
                {
                    rows.addAll(RECORDS.getOrDefault(recordKey(moduleCode, stringValue(feature.get("code"))), Collections.emptyList()));
                }
            }
        }
        rows = rows.stream()
                .filter(row -> matches(row, keyword, status))
                .sorted((a, b) -> stringValue(b.get("updateTime")).compareTo(stringValue(a.get("updateTime"))))
                .collect(Collectors.toList());
        int total = rows.size();
        int from = Math.max(0, (pageNum - 1) * pageSize);
        int to = Math.min(total, from + pageSize);
        List<Map<String, Object>> pageRows = from >= total ? Collections.emptyList() : rows.subList(from, to);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", pageRows);
        ajax.put("total", total);
        return ajax;
    }

    @GetMapping("/records/{id}")
    public AjaxResult record(@PathVariable Long id)
    {
        Map<String, Object> row = findRecord(id);
        return row == null ? AjaxResult.error("记录不存在") : AjaxResult.success(row);
    }

    @PostMapping("/records")
    public AjaxResult addRecord(@RequestBody Map<String, Object> body)
    {
        String moduleCode = stringValue(body.get("moduleCode"));
        String featureCode = stringValue(body.get("featureCode"));
        if (moduleCode.isBlank() || featureCode.isBlank())
        {
            return AjaxResult.error("模块编码和功能编码不能为空");
        }
        Map<String, Object> row = normalizeRecord(body, ID.incrementAndGet());
        RECORDS.computeIfAbsent(recordKey(moduleCode, featureCode), key -> Collections.synchronizedList(new ArrayList<>())).add(row);
        return AjaxResult.success(row);
    }

    @PutMapping("/records/{id}")
    public AjaxResult editRecord(@PathVariable Long id, @RequestBody Map<String, Object> body)
    {
        Map<String, Object> row = findRecord(id);
        if (row == null)
        {
            return AjaxResult.error("记录不存在");
        }
        row.putAll(body);
        row.put("id", id);
        row.put("updateTime", now());
        if (!row.containsKey("status") || stringValue(row.get("status")).isBlank())
        {
            row.put("status", "draft");
        }
        return AjaxResult.success(row);
    }

    @DeleteMapping("/records/{ids}")
    public AjaxResult removeRecord(@PathVariable Long[] ids)
    {
        List<Long> idList = Arrays.asList(ids);
        for (List<Map<String, Object>> rows : RECORDS.values())
        {
            rows.removeIf(row -> idList.contains(Long.valueOf(String.valueOf(row.get("id")))));
        }
        return AjaxResult.success();
    }

    @PostMapping("/records/{id}/action/{action}")
    public AjaxResult action(@PathVariable Long id, @PathVariable String action)
    {
        Map<String, Object> row = findRecord(id);
        if (row == null)
        {
            return AjaxResult.error("记录不存在");
        }
        String status = switch (action)
        {
            case "submit" -> "submitted";
            case "confirm" -> "confirmed";
            case "archive" -> "archived";
            case "return" -> "draft";
            default -> "processing";
        };
        row.put("status", status);
        row.put("workflowNode", workflowNode(status));
        row.put("updateTime", now());
        return AjaxResult.success(row);
    }

    private static List<Map<String, Object>> loadModules()
    {
        try
        {
            ClassPathResource resource = new ClassPathResource("threebase/modules.json");
            try (InputStream inputStream = resource.getInputStream())
            {
                String text = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                Map<String, Object> root = JSON.parseObject(text, Map.class);
                return listValue(root.get("modules"));
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException("无法加载体系三基功能清单", e);
        }
    }

    private static void ensureSeed(String moduleCode, String featureCode)
    {
        if (featureCode != null && !featureCode.isBlank())
        {
            RECORDS.computeIfAbsent(recordKey(moduleCode, featureCode), key -> seedRows(moduleCode, featureCode));
        }
    }

    private static List<Map<String, Object>> seedRows(String moduleCode, String featureCode)
    {
        Map<String, Object> module = findModule(moduleCode);
        Map<String, Object> feature = findFeature(module, featureCode);
        List<Map<String, Object>> rows = Collections.synchronizedList(new ArrayList<>());
        if (module != null && feature != null)
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("moduleCode", moduleCode);
            row.put("moduleName", module.get("name"));
            row.put("featureCode", featureCode);
            row.put("featureName", feature.get("name"));
            row.put("title", feature.get("name") + "样例记录");
            row.put("responsibleDept", module.get("owner"));
            row.put("owner", "管理员");
            row.put("planTime", LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            row.put("status", "draft");
            row.put("workflowNode", "发起");
            row.put("description", feature.get("description"));
            row.put("attachmentCount", 0);
            row.put("remark", "依据功能清单生成的初始样例，可编辑或删除。");
            rows.add(normalizeRecord(row, ID.incrementAndGet()));
        }
        return rows;
    }

    private static Map<String, Object> normalizeRecord(Map<String, Object> source, Long id)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", id);
        row.put("moduleCode", stringValue(source.get("moduleCode")));
        row.put("moduleName", valueOrDefault(source.get("moduleName"), moduleName(stringValue(source.get("moduleCode")))));
        row.put("featureCode", stringValue(source.get("featureCode")));
        row.put("featureName", valueOrDefault(source.get("featureName"), featureName(stringValue(source.get("moduleCode")), stringValue(source.get("featureCode")))));
        row.put("title", valueOrDefault(source.get("title"), row.get("featureName")));
        row.put("responsibleDept", valueOrDefault(source.get("responsibleDept"), "待配置"));
        row.put("owner", valueOrDefault(source.get("owner"), "待指派"));
        row.put("planTime", valueOrDefault(source.get("planTime"), LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        row.put("status", valueOrDefault(source.get("status"), "draft"));
        row.put("workflowNode", valueOrDefault(source.get("workflowNode"), workflowNode(stringValue(row.get("status")))));
        row.put("description", valueOrDefault(source.get("description"), ""));
        row.put("attachmentCount", source.getOrDefault("attachmentCount", 0));
        row.put("remark", valueOrDefault(source.get("remark"), ""));
        row.put("createTime", valueOrDefault(source.get("createTime"), now()));
        row.put("updateTime", now());
        return row;
    }

    private static boolean matches(Map<String, Object> row, String keyword, String status)
    {
        if (status != null && !status.isBlank() && !status.equals(row.get("status")))
        {
            return false;
        }
        if (keyword == null || keyword.isBlank())
        {
            return true;
        }
        String raw = JSON.toJSONString(row).toLowerCase();
        return raw.contains(keyword.toLowerCase());
    }

    private static List<Map<String, Object>> integrationsData()
    {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(integration("sso", "统一身份认证", "connected", "用户、部门、岗位角色同步"));
        rows.add(integration("data-center", "数据中台", "reserved", "组织架构、基础数据、指标数据"));
        rows.add(integration("bpm", "BPM 流程平台", "reserved", "审批、确认、归档流程"));
        rows.add(integration("integrated-mgmt", "一体化管理平台", "reserved", "制度、任务、台账数据联动"));
        rows.add(integration("work-ticket", "作业票/巡检系统", "reserved", "现场作业与巡检数据接入"));
        rows.add(integration("message", "消息通知平台", "reserved", "待办、提醒、催办消息"));
        return rows;
    }

    private static Map<String, Object> integration(String code, String name, String status, String scope)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("code", code);
        row.put("name", name);
        row.put("status", status);
        row.put("scope", scope);
        return row;
    }

    private static Map<String, Object> todo(String title, String desc, String time, String type)
    {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("title", title);
        row.put("desc", desc);
        row.put("time", time);
        row.put("type", type);
        return row;
    }

    private static String workflowNode(String status)
    {
        return switch (status)
        {
            case "submitted" -> "部门确认";
            case "confirmed" -> "归档确认";
            case "archived" -> "已归档";
            case "processing" -> "处理中";
            default -> "发起";
        };
    }

    private static String recordKey(String moduleCode, String featureCode)
    {
        return moduleCode + ":" + featureCode;
    }

    private static String moduleName(String moduleCode)
    {
        Map<String, Object> module = findModule(moduleCode);
        return module == null ? "" : stringValue(module.get("name"));
    }

    private static String featureName(String moduleCode, String featureCode)
    {
        Map<String, Object> feature = findFeature(findModule(moduleCode), featureCode);
        return feature == null ? "" : stringValue(feature.get("name"));
    }

    private static Map<String, Object> findModule(String moduleCode)
    {
        return MODULES.stream().filter(item -> Objects.equals(moduleCode, item.get("code"))).findFirst().orElse(null);
    }

    private static Map<String, Object> findFeature(Map<String, Object> module, String featureCode)
    {
        if (module == null)
        {
            return null;
        }
        return listValue(module.get("features")).stream().filter(item -> Objects.equals(featureCode, item.get("code"))).findFirst().orElse(null);
    }

    private static Map<String, Object> findRecord(Long id)
    {
        for (List<Map<String, Object>> rows : RECORDS.values())
        {
            for (Map<String, Object> row : rows)
            {
                if (Objects.equals(Long.valueOf(String.valueOf(row.get("id"))), id))
                {
                    return row;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> listValue(Object value)
    {
        if (value instanceof List<?> list)
        {
            return (List<Map<String, Object>>) list;
        }
        return Collections.emptyList();
    }

    private static String stringValue(Object value)
    {
        return value == null ? "" : String.valueOf(value);
    }

    private static Object valueOrDefault(Object value, Object defaultValue)
    {
        return value == null || String.valueOf(value).isBlank() ? defaultValue : value;
    }

    private static String now()
    {
        return LocalDateTime.now().format(TIME_FORMAT);
    }
}
