package com.ruoyi.web.controller.threebase;

import java.io.ByteArrayInputStream;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.BtTaskInfo;
import com.ruoyi.system.service.IBtTaskInfoService;

/**
 * 任务要素信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/threebase/taskInfo")
public class BtTaskInfoController extends BaseController
{
    @Autowired
    private IBtTaskInfoService btTaskInfoService;

    /**
     * 获取任务要素列表
     */
    @PreAuthorize("@ss.hasPermi('threebase:taskInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(BtTaskInfo btTaskInfo)
    {
        startPage();
        List<BtTaskInfo> list = btTaskInfoService.selectBtTaskInfoList(btTaskInfo);
        return getDataTable(list);
    }

    @Log(title = "工作任务管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('threebase:taskInfo:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BtTaskInfo btTaskInfo)
    {
        List<BtTaskInfo> list = btTaskInfoService.selectBtTaskInfoList(btTaskInfo);
        ExcelUtil<BtTaskInfo> util = new ExcelUtil<BtTaskInfo>(BtTaskInfo.class);
        util.exportExcel(response, list, "任务要素数据");
    }

    @Log(title = "工作任务管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('threebase:taskInfo:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        byte[] excelBytes = file.getBytes();
        int titleNum = detectHeaderRowNum(excelBytes);
        ExcelUtil<BtTaskInfo> util = new ExcelUtil<BtTaskInfo>(BtTaskInfo.class);
        List<BtTaskInfo> btTaskInfoList = util.importExcel(new ByteArrayInputStream(excelBytes), titleNum);
        String message = btTaskInfoService.importBtTaskInfo(btTaskInfoList, updateSupport, getUsername());
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<BtTaskInfo> util = new ExcelUtil<BtTaskInfo>(BtTaskInfo.class);
        util.importTemplateExcel(response, "任务要素数据");
    }

    private int detectHeaderRowNum(byte[] excelBytes) throws Exception
    {
        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelBytes)))
        {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null)
                {
                    continue;
                }
                short firstCellNum = row.getFirstCellNum();
                short lastCellNum = row.getLastCellNum();
                if (firstCellNum < 0 || lastCellNum < 0)
                {
                    continue;
                }
                for (int j = firstCellNum; j < lastCellNum; j++)
                {
                    String cellValue = formatter.formatCellValue(row.getCell(j)).trim();
                    if ("业务类别".equals(cellValue))
                    {
                        return i;
                    }
                }
            }
        }
        throw new ServiceException("未找到包含“业务类别”的表头行，请检查 Excel 文件格式。");
    }

    /**
     * 根据任务要素编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('threebase:taskInfo:query')")
    @GetMapping(value = "/{taskInfoId}")
    public AjaxResult getInfo(@PathVariable Long taskInfoId)
    {
        return success(btTaskInfoService.selectBtTaskInfoById(taskInfoId));
    }

    /**
     * 新增任务要素
     */
    @PreAuthorize("@ss.hasPermi('threebase:taskInfo:add')")
    @Log(title = "工作任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BtTaskInfo btTaskInfo)
    {
        return toAjax(btTaskInfoService.insertBtTaskInfo(btTaskInfo));
    }

    /**
     * 修改任务要素
     */
    @PreAuthorize("@ss.hasPermi('threebase:taskInfo:edit')")
    @Log(title = "工作任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BtTaskInfo btTaskInfo)
    {
        return toAjax(btTaskInfoService.updateBtTaskInfo(btTaskInfo));
    }

    /**
     * 删除任务要素
     */
    @PreAuthorize("@ss.hasPermi('threebase:taskInfo:remove')")
    @Log(title = "工作任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskInfoIds}")
    public AjaxResult remove(@PathVariable Long[] taskInfoIds)
    {
        return toAjax(btTaskInfoService.deleteBtTaskInfoByIds(taskInfoIds));
    }
}
