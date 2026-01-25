package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.StallApplication;

public interface StallApplicationService extends IService<StallApplication> {
    
    boolean submit(StallApplication application);
    
    IPage<StallApplication> pageList(Integer pageNum, Integer pageSize, Long userId, Integer status);
    
    IPage<StallApplication> pageListForAdmin(Integer pageNum, Integer pageSize, Integer status);
    
    boolean review(Long id, Integer status, String reviewOpinion, Long reviewerId);
    
    boolean cancel(Long id, Long userId);
    
    StallApplication getDetailById(Long id);
}
