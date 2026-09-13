package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.BtTaskInfo;

/**
 * 任务要素 服务层
 *
 * @author ruoyi
 */
public interface IBtTaskInfoService
{
    public List<BtTaskInfo> selectBtTaskInfoList(BtTaskInfo btTaskInfo);

    public BtTaskInfo selectBtTaskInfoById(Long taskInfoId);

    public int insertBtTaskInfo(BtTaskInfo btTaskInfo);

    public String importBtTaskInfo(List<BtTaskInfo> btTaskInfoList, boolean updateSupport, String operName);

    public int updateBtTaskInfo(BtTaskInfo btTaskInfo);

    public int deleteBtTaskInfoByIds(Long[] taskInfoIds);

    public int deleteBtTaskInfoById(Long taskInfoId);
}
