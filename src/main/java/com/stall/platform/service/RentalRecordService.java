package com.stall.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.RentalRecord;

public interface RentalRecordService extends IService<RentalRecord> {
    
    IPage<RentalRecord> pageList(Integer pageNum, Integer pageSize, Long userId, Long stallId);
    
    IPage<RentalRecord> pageListForAdmin(Integer pageNum, Integer pageSize, Long stallId, Integer status);
    
    boolean updatePaymentStatus(Long id, Integer paymentStatus);
    
    boolean terminate(Long id);
    
    RentalRecord getDetailById(Long id);
}
