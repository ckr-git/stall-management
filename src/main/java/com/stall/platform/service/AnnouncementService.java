package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.Announcement;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    
    IPage<Announcement> pageList(Integer pageNum, Integer pageSize, Integer type, Integer status);
    
    List<Announcement> listPublished(Integer type);
    
    boolean publish(Announcement announcement);
    
    boolean updateStatus(Long id, Integer status);
    
    Announcement getDetailById(Long id);
}
