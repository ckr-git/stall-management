package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.HygieneInspection;

public interface HygieneInspectionService extends IService<HygieneInspection> {
    
    IPage<HygieneInspection> pageList(Integer pageNum, Integer pageSize, Long stallId, String result);
    
    boolean addInspection(HygieneInspection inspection);
    
    boolean updateRectification(Long id, Integer status);
    
    HygieneInspection getDetailById(Long id);
}
