package com.ruoyi.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务要素对象 bt_task_info
 *
 * @author ruoyi
 */
public class BtTaskInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务要素ID */
    private Long taskInfoId;

    /** 业务类别 */
    @Excel(name = "业务类别")
    private String businessCategory;

    /** 流程组 */
    @Excel(name = "流程组")
    private String processGroup;

    /** 基本流程 */
    @Excel(name = "基本流程")
    private String basicProcess;

    /** 专项流程 */
    @Excel(name = "专项流程")
    private String specialProcess;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 三标手册 */
    @Excel(name = "三标手册")
    private String standardManual;

    /** 三基要求 */
    @Excel(name = "三基要求")
    private String threeBasisRequirement;

    /** 一体化体系要素 */
    @Excel(name = "一体化体系要素")
    private String integratedSystemElement;

    /** HSE体系要素 */
    @Excel(name = "HSE体系要素")
    private String hseSystemElement;

    /** 执行岗位 */
    @Excel(name = "执行岗位")
    private String executionPost;

    /** 人员 */
    @Excel(name = "人员")
    private String personnel;

    /** 任务权限 */
    @Excel(name = "任务权限")
    private String taskPermission;

    /** 工作依据及标准 */
    @Excel(name = "工作依据及标准")
    private String workBasisStandard;

    /** 工作周期/频次 */
    @Excel(name = "工作周期/频次")
    private String workCycleFrequency;

    /** 触发时间 */
    @Excel(name = "触发时间")
    private String triggerTime;

    /** 规定完成时间 */
    @Excel(name = "规定完成时间")
    private String requiredFinishTime;

    /** 标准化表单 */
    @Excel(name = "标准化表单")
    private String standardForm;

    /** 信息系统 */
    @Excel(name = "信息系统")
    private String informationSystem;

    /** 机构设置 */
    @Excel(name = "机构设置")
    private String organizationSetting;

    /** 职能模块DL1（52项） */
    @Excel(name = "职能模块DL1（52项）")
    private String dl1FunctionModule;

    /** 业务类别DL2（128项） */
    @Excel(name = "业务类别DL2（128项）")
    private String dl2BusinessCategory;

    /** 基本业务DL3（363项） */
    @Excel(name = "基本业务DL3（363项）")
    private String dl3BasicBusiness;

    public Long getTaskInfoId()
    {
        return taskInfoId;
    }

    public void setTaskInfoId(Long taskInfoId)
    {
        this.taskInfoId = taskInfoId;
    }

    public String getBusinessCategory()
    {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory)
    {
        this.businessCategory = businessCategory;
    }

    public String getProcessGroup()
    {
        return processGroup;
    }

    public void setProcessGroup(String processGroup)
    {
        this.processGroup = processGroup;
    }

    public String getBasicProcess()
    {
        return basicProcess;
    }

    public void setBasicProcess(String basicProcess)
    {
        this.basicProcess = basicProcess;
    }

    public String getSpecialProcess()
    {
        return specialProcess;
    }

    public void setSpecialProcess(String specialProcess)
    {
        this.specialProcess = specialProcess;
    }

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 500, message = "任务名称长度不能超过500个字符")
    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getStandardManual()
    {
        return standardManual;
    }

    public void setStandardManual(String standardManual)
    {
        this.standardManual = standardManual;
    }

    public String getThreeBasisRequirement()
    {
        return threeBasisRequirement;
    }

    public void setThreeBasisRequirement(String threeBasisRequirement)
    {
        this.threeBasisRequirement = threeBasisRequirement;
    }

    public String getIntegratedSystemElement()
    {
        return integratedSystemElement;
    }

    public void setIntegratedSystemElement(String integratedSystemElement)
    {
        this.integratedSystemElement = integratedSystemElement;
    }

    public String getHseSystemElement()
    {
        return hseSystemElement;
    }

    public void setHseSystemElement(String hseSystemElement)
    {
        this.hseSystemElement = hseSystemElement;
    }

    public String getExecutionPost()
    {
        return executionPost;
    }

    public void setExecutionPost(String executionPost)
    {
        this.executionPost = executionPost;
    }

    public String getPersonnel()
    {
        return personnel;
    }

    public void setPersonnel(String personnel)
    {
        this.personnel = personnel;
    }

    public String getTaskPermission()
    {
        return taskPermission;
    }

    public void setTaskPermission(String taskPermission)
    {
        this.taskPermission = taskPermission;
    }

    public String getWorkBasisStandard()
    {
        return workBasisStandard;
    }

    public void setWorkBasisStandard(String workBasisStandard)
    {
        this.workBasisStandard = workBasisStandard;
    }

    public String getWorkCycleFrequency()
    {
        return workCycleFrequency;
    }

    public void setWorkCycleFrequency(String workCycleFrequency)
    {
        this.workCycleFrequency = workCycleFrequency;
    }

    public String getTriggerTime()
    {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime)
    {
        this.triggerTime = triggerTime;
    }

    public String getRequiredFinishTime()
    {
        return requiredFinishTime;
    }

    public void setRequiredFinishTime(String requiredFinishTime)
    {
        this.requiredFinishTime = requiredFinishTime;
    }

    public String getStandardForm()
    {
        return standardForm;
    }

    public void setStandardForm(String standardForm)
    {
        this.standardForm = standardForm;
    }

    public String getInformationSystem()
    {
        return informationSystem;
    }

    public void setInformationSystem(String informationSystem)
    {
        this.informationSystem = informationSystem;
    }

    public String getOrganizationSetting()
    {
        return organizationSetting;
    }

    public void setOrganizationSetting(String organizationSetting)
    {
        this.organizationSetting = organizationSetting;
    }

    public String getDl1FunctionModule()
    {
        return dl1FunctionModule;
    }

    public void setDl1FunctionModule(String dl1FunctionModule)
    {
        this.dl1FunctionModule = dl1FunctionModule;
    }

    public String getDl2BusinessCategory()
    {
        return dl2BusinessCategory;
    }

    public void setDl2BusinessCategory(String dl2BusinessCategory)
    {
        this.dl2BusinessCategory = dl2BusinessCategory;
    }

    public String getDl3BasicBusiness()
    {
        return dl3BasicBusiness;
    }

    public void setDl3BasicBusiness(String dl3BasicBusiness)
    {
        this.dl3BasicBusiness = dl3BasicBusiness;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("taskInfoId", getTaskInfoId())
            .append("businessCategory", getBusinessCategory())
            .append("processGroup", getProcessGroup())
            .append("basicProcess", getBasicProcess())
            .append("specialProcess", getSpecialProcess())
            .append("taskName", getTaskName())
            .append("standardManual", getStandardManual())
            .append("threeBasisRequirement", getThreeBasisRequirement())
            .append("integratedSystemElement", getIntegratedSystemElement())
            .append("hseSystemElement", getHseSystemElement())
            .append("executionPost", getExecutionPost())
            .append("personnel", getPersonnel())
            .append("taskPermission", getTaskPermission())
            .append("workBasisStandard", getWorkBasisStandard())
            .append("workCycleFrequency", getWorkCycleFrequency())
            .append("triggerTime", getTriggerTime())
            .append("requiredFinishTime", getRequiredFinishTime())
            .append("standardForm", getStandardForm())
            .append("informationSystem", getInformationSystem())
            .append("organizationSetting", getOrganizationSetting())
            .append("dl1FunctionModule", getDl1FunctionModule())
            .append("dl2BusinessCategory", getDl2BusinessCategory())
            .append("dl3BasicBusiness", getDl3BasicBusiness())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
