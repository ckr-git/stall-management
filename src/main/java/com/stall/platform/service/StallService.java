package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.Stall;

import java.util.List;

public interface StallService extends IService<Stall> {
    
    IPage<Stall> pageList(Integer pageNum, Integer pageSize, Long typeId, Integer status, String keyword);
    
    List<Stall> listAvailable();
    
    Stall getDetailById(Long id);
    
    boolean updateStatus(Long id, Integer status);
}
