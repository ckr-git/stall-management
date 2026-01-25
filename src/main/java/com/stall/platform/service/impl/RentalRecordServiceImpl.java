package com.stall.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.RentalRecord;
import com.stall.platform.entity.Stall;
import com.stall.platform.entity.User;
import com.stall.platform.mapper.RentalRecordMapper;
import com.stall.platform.service.RentalRecordService;
import com.stall.platform.service.StallService;
import com.stall.platform.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentalRecordServiceImpl extends ServiceImpl<RentalRecordMapper, RentalRecord> 
        implements RentalRecordService {
    
    private final UserService userService;
    private final StallService stallService;
    
    public RentalRecordServiceImpl(UserService userService, @Lazy StallService stallService) {
        this.userService = userService;
        this.stallService = stallService;
    }
    
    @Override
    public IPage<RentalRecord> pageList(Integer pageNum, Integer pageSize, Long userId, Long stallId) {
        LambdaQueryWrapper<RentalRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(RentalRecord::getUserId, userId);
        }
        if (stallId != null) {
            wrapper.eq(RentalRecord::getStallId, stallId);
        }
        wrapper.orderByDesc(RentalRecord::getCreateTime);
        IPage<RentalRecord> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillDetails);
        return page;
    }
    
    @Override
    public IPage<RentalRecord> pageListForAdmin(Integer pageNum, Integer pageSize, Long stallId, Integer status) {
        LambdaQueryWrapper<RentalRecord> wrapper = new LambdaQueryWrapper<>();
        if (stallId != null) {
            wrapper.eq(RentalRecord::getStallId, stallId);
        }
        if (status != null) {
            wrapper.eq(RentalRecord::getStatus, status);
        }
        wrapper.orderByDesc(RentalRecord::getCreateTime);
        IPage<RentalRecord> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillDetails);
        return page;
    }
    
    @Override
    public boolean updatePaymentStatus(Long id, Integer paymentStatus) {
        RentalRecord record = new RentalRecord();
        record.setId(id);
        record.setPaymentStatus(paymentStatus);
        return updateById(record);
    }
    
    @Override
    @Transactional
    public boolean terminate(Long id) {
        RentalRecord record = getById(id);
        if (record == null || record.getStatus() != 1) {
            return false;
        }
        record.setStatus(3); // 提前终止
        boolean result = updateById(record);
        
        // 释放摊位
        if (result) {
            stallService.updateStatus(record.getStallId(), 0);
        }
        return result;
    }
    
    @Override
    public RentalRecord getDetailById(Long id) {
        RentalRecord record = getById(id);
        if (record != null) {
            fillDetails(record);
        }
        return record;
    }
    
    private void fillDetails(RentalRecord record) {
        if (record.getUserId() != null) {
            User user = userService.getById(record.getUserId());
            if (user != null) {
                record.setUsername(user.getUsername());
            }
        }
        if (record.getStallId() != null) {
            Stall stall = stallService.getById(record.getStallId());
            if (stall != null) {
                record.setStallName(stall.getName());
                record.setStallNo(stall.getStallNo());
            }
        }
    }
}
