package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.Feedback;

public interface FeedbackService extends IService<Feedback> {
    
    boolean submit(Feedback feedback);
    
    IPage<Feedback> pageList(Integer pageNum, Integer pageSize, Long userId, Integer type, Integer status);
    
    IPage<Feedback> pageListForAdmin(Integer pageNum, Integer pageSize, Integer type, Integer status);
    
    boolean reply(Long id, String reply, Long handlerId);
    
    boolean updateStatus(Long id, Integer status);
    
    Feedback getDetailById(Long id);
}
