package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.BtTaskInfo;

/**
 * 任务要素 数据层
 *
 * @author ruoyi
 */
public interface BtTaskInfoMapper
{
    public List<BtTaskInfo> selectBtTaskInfoList(BtTaskInfo btTaskInfo);

    public BtTaskInfo selectBtTaskInfoById(Long taskInfoId);

    public int insertBtTaskInfo(BtTaskInfo btTaskInfo);

    public int batchInsertBtTaskInfo(List<BtTaskInfo> btTaskInfoList);

    public int updateBtTaskInfo(BtTaskInfo btTaskInfo);

    public int deleteBtTaskInfoById(Long taskInfoId);

    public int deleteBtTaskInfoByIds(Long[] taskInfoIds);
}
