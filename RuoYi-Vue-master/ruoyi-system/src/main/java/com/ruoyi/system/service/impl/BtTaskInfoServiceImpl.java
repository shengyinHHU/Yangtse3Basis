package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BtTaskInfo;
import com.ruoyi.system.mapper.BtTaskInfoMapper;
import com.ruoyi.system.service.IBtTaskInfoService;

/**
 * 任务要素 服务层处理
 *
 * @author ruoyi
 */
@Service
public class BtTaskInfoServiceImpl implements IBtTaskInfoService
{
    @Autowired
    private BtTaskInfoMapper btTaskInfoMapper;

    @Override
    public List<BtTaskInfo> selectBtTaskInfoList(BtTaskInfo btTaskInfo)
    {
        return btTaskInfoMapper.selectBtTaskInfoList(btTaskInfo);
    }

    @Override
    public BtTaskInfo selectBtTaskInfoById(Long taskInfoId)
    {
        return btTaskInfoMapper.selectBtTaskInfoById(taskInfoId);
    }

    @Override
    public int insertBtTaskInfo(BtTaskInfo btTaskInfo)
    {
        return btTaskInfoMapper.insertBtTaskInfo(btTaskInfo);
    }

    @Override
    public String importBtTaskInfo(List<BtTaskInfo> btTaskInfoList, boolean updateSupport, String operName)
    {
        if (StringUtils.isNull(btTaskInfoList) || btTaskInfoList.size() == 0)
        {
            throw new ServiceException("导入工作任务数据不能为空！");
        }
        List<BtTaskInfo> validList = btTaskInfoList.stream()
                .filter(item -> StringUtils.isNotBlank(item.getTaskName()))
                .collect(Collectors.toList());
        if (validList.size() == 0)
        {
            throw new ServiceException("未读取到有效的工作任务数据，请检查 Excel 表头与任务名称列。");
        }
        btTaskInfoMapper.batchInsertBtTaskInfo(validList);
        return "成功导入 " + validList.size() + " 条工作任务数据";
    }

    @Override
    public int updateBtTaskInfo(BtTaskInfo btTaskInfo)
    {
        return btTaskInfoMapper.updateBtTaskInfo(btTaskInfo);
    }

    @Override
    public int deleteBtTaskInfoByIds(Long[] taskInfoIds)
    {
        return btTaskInfoMapper.deleteBtTaskInfoByIds(taskInfoIds);
    }

    @Override
    public int deleteBtTaskInfoById(Long taskInfoId)
    {
        return btTaskInfoMapper.deleteBtTaskInfoById(taskInfoId);
    }
}
