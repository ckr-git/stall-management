package com.stall.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.Feedback;
import com.stall.platform.entity.Stall;
import com.stall.platform.entity.User;
import com.stall.platform.mapper.FeedbackMapper;
import com.stall.platform.service.FeedbackService;
import com.stall.platform.service.StallService;
import com.stall.platform.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
    
    private final StallService stallService;
    private final UserService userService;
    
    public FeedbackServiceImpl(StallService stallService, UserService userService) {
        this.stallService = stallService;
        this.userService = userService;
    }
    
    @Override
    public boolean submit(Feedback feedback) {
        feedback.setStatus(0); // 待处理
        return save(feedback);
    }
    
    @Override
    public IPage<Feedback> pageList(Integer pageNum, Integer pageSize, Long userId, Integer type, Integer status) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId);
        if (type != null) {
            wrapper.eq(Feedback::getType, type);
        }
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        IPage<Feedback> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillDetails);
        return page;
    }
    
    @Override
    public IPage<Feedback> pageListForAdmin(Integer pageNum, Integer pageSize, Integer type, Integer status) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Feedback::getType, type);
        }
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        IPage<Feedback> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillDetails);
        return page;
    }
    
    @Override
    public boolean reply(Long id, String reply, Long handlerId) {
        Feedback feedback = getById(id);
        if (feedback == null) {
            return false;
        }
        feedback.setReply(reply);
        feedback.setHandlerId(handlerId);
        feedback.setHandleTime(LocalDateTime.now());
        feedback.setStatus(2); // 已处理
        return updateById(feedback);
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setStatus(status);
        return updateById(feedback);
    }
    
    @Override
    public Feedback getDetailById(Long id) {
        Feedback feedback = getById(id);
        if (feedback != null) {
            fillDetails(feedback);
        }
        return feedback;
    }
    
    private void fillDetails(Feedback feedback) {
        if (feedback.getUserId() != null) {
            User user = userService.getById(feedback.getUserId());
            if (user != null) {
                feedback.setUsername(user.getUsername());
            }
        }
        if (feedback.getStallId() != null) {
            Stall stall = stallService.getById(feedback.getStallId());
            if (stall != null) {
                feedback.setStallName(stall.getName());
            }
        }
        if (feedback.getHandlerId() != null) {
            User handler = userService.getById(feedback.getHandlerId());
            if (handler != null) {
                feedback.setHandlerName(handler.getNickname());
            }
        }
    }
}
