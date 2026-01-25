package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.SystemLog;

public interface SystemLogService extends IService<SystemLog> {
    
    IPage<SystemLog> pageList(Integer pageNum, Integer pageSize, Long userId, String operation);
    
    void saveLog(SystemLog log);
}
