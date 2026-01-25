package com.stall.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.Announcement;
import com.stall.platform.entity.User;
import com.stall.platform.mapper.AnnouncementMapper;
import com.stall.platform.service.AnnouncementService;
import com.stall.platform.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> 
        implements AnnouncementService {
    
    private final UserService userService;
    
    public AnnouncementServiceImpl(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public IPage<Announcement> pageList(Integer pageNum, Integer pageSize, Integer type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getPriority).orderByDesc(Announcement::getPublishTime);
        IPage<Announcement> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillPublisherName);
        return page;
    }
    
    @Override
    public List<Announcement> listPublished(Integer type) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1); // 已发布
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        wrapper.orderByDesc(Announcement::getPriority).orderByDesc(Announcement::getPublishTime);
        List<Announcement> list = list(wrapper);
        list.forEach(this::fillPublisherName);
        return list;
    }
    
    @Override
    public boolean publish(Announcement announcement) {
        announcement.setStatus(1); // 已发布
        announcement.setPublishTime(LocalDateTime.now());
        return save(announcement);
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(status);
        if (status == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        return updateById(announcement);
    }
    
    @Override
    public Announcement getDetailById(Long id) {
        Announcement announcement = getById(id);
        if (announcement != null) {
            fillPublisherName(announcement);
        }
        return announcement;
    }
    
    private void fillPublisherName(Announcement announcement) {
        if (announcement.getPublisherId() != null) {
            User user = userService.getById(announcement.getPublisherId());
            if (user != null) {
                announcement.setPublisherName(user.getNickname());
            }
        }
    }
}
