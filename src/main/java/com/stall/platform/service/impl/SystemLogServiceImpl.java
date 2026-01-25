package com.stall.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.SystemLog;
import com.stall.platform.mapper.SystemLogMapper;
import com.stall.platform.service.SystemLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements SystemLogService {
    
    @Override
    public IPage<SystemLog> pageList(Integer pageNum, Integer pageSize, Long userId, String operation) {
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(SystemLog::getUserId, userId);
        }
        if (StringUtils.hasText(operation)) {
            wrapper.like(SystemLog::getOperation, operation);
        }
        wrapper.orderByDesc(SystemLog::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    @Async
    public void saveLog(SystemLog log) {
        save(log);
    }
}
