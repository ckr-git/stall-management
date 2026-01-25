package com.stall.platform.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.*;
import com.stall.platform.mapper.StallApplicationMapper;
import com.stall.platform.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class StallApplicationServiceImpl extends ServiceImpl<StallApplicationMapper, StallApplication> 
        implements StallApplicationService {
    
    private final StallService stallService;
    private final UserService userService;
    private final RentalRecordService rentalRecordService;
    
    public StallApplicationServiceImpl(StallService stallService, UserService userService,
                                         RentalRecordService rentalRecordService) {
        this.stallService = stallService;
        this.userService = userService;
        this.rentalRecordService = rentalRecordService;
    }
    
    @Override
    public boolean submit(StallApplication application) {
        // 生成申请编号
        application.setApplicationNo("APP" + IdUtil.getSnowflakeNextIdStr());
        application.setStatus(0); // 待审核
        return save(application);
    }
    
    @Override
    public IPage<StallApplication> pageList(Integer pageNum, Integer pageSize, Long userId, Integer status) {
        LambdaQueryWrapper<StallApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StallApplication::getUserId, userId);
        if (status != null) {
            wrapper.eq(StallApplication::getStatus, status);
        }
        wrapper.orderByDesc(StallApplication::getCreateTime);
        IPage<StallApplication> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillDetails);
        return page;
    }
    
    @Override
    public IPage<StallApplication> pageListForAdmin(Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<StallApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(StallApplication::getStatus, status);
        }
        wrapper.orderByDesc(StallApplication::getCreateTime);
        IPage<StallApplication> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillDetails);
        return page;
    }
    
    @Override
    @Transactional
    public boolean review(Long id, Integer status, String reviewOpinion, Long reviewerId) {
        StallApplication application = getById(id);
        if (application == null || application.getStatus() != 0) {
            return false;
        }
        
        application.setStatus(status);
        application.setReviewOpinion(reviewOpinion);
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());
        boolean result = updateById(application);
        
        // 如果审核通过，更新摊位状态为已租用，并创建租赁记录
        if (result && status == 1) {
            stallService.updateStatus(application.getStallId(), 1);
            
            // 创建租赁记录
            Stall stall = stallService.getById(application.getStallId());
            RentalRecord record = new RentalRecord();
            record.setApplicationId(application.getId());
            record.setUserId(application.getUserId());
            record.setStallId(application.getStallId());
            record.setStartDate(application.getStartDate());
            record.setEndDate(application.getEndDate());
            
            // 计算租金
            long months = ChronoUnit.MONTHS.between(application.getStartDate(), application.getEndDate());
            if (months < 1) months = 1;
            record.setRentAmount(stall.getRentPrice().multiply(BigDecimal.valueOf(months)));
            record.setDeposit(stall.getRentPrice());
            record.setPaymentStatus(0);
            record.setStatus(1);
            rentalRecordService.save(record);
        }
        
        return result;
    }
    
    @Override
    public boolean cancel(Long id, Long userId) {
        StallApplication application = getById(id);
        if (application == null || !application.getUserId().equals(userId) || application.getStatus() != 0) {
            return false;
        }
        application.setStatus(3); // 已取消
        return updateById(application);
    }
    
    @Override
    public StallApplication getDetailById(Long id) {
        StallApplication application = getById(id);
        if (application != null) {
            fillDetails(application);
        }
        return application;
    }
    
    private void fillDetails(StallApplication application) {
        if (application.getUserId() != null) {
            User user = userService.getById(application.getUserId());
            if (user != null) {
                application.setUsername(user.getUsername());
            }
        }
        if (application.getStallId() != null) {
            Stall stall = stallService.getById(application.getStallId());
            if (stall != null) {
                application.setStallName(stall.getName());
                application.setStallNo(stall.getStallNo());
            }
        }
    }
}
