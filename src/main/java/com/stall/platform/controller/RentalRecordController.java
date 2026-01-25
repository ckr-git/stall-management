package com.stall.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stall.platform.common.Result;
import com.stall.platform.entity.RentalRecord;
import com.stall.platform.security.LoginUser;
import com.stall.platform.service.RentalRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rental")
public class RentalRecordController {
    
    private final RentalRecordService rentalRecordService;
    
    public RentalRecordController(RentalRecordService rentalRecordService) {
        this.rentalRecordService = rentalRecordService;
    }
    
    @GetMapping("/my")
    public Result<?> myList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        IPage<RentalRecord> page = rentalRecordService.pageList(pageNum, pageSize, 
                loginUser.getUserId(), null);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        RentalRecord record = rentalRecordService.getDetailById(id);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("租赁记录不存在");
    }
    
    // ========== 管理员接口 ==========
    
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long stallId,
            @RequestParam(required = false) Integer status) {
        IPage<RentalRecord> page = rentalRecordService.pageListForAdmin(pageNum, pageSize, stallId, status);
        return Result.success(page);
    }
    
    @PutMapping("/admin/{id}/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updatePayment(@PathVariable Long id, @RequestParam Integer paymentStatus) {
        boolean success = rentalRecordService.updatePaymentStatus(id, paymentStatus);
        return success ? Result.success("支付状态更新成功") : Result.error("更新失败");
    }
    
    @PutMapping("/admin/{id}/terminate")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> terminate(@PathVariable Long id) {
        boolean success = rentalRecordService.terminate(id);
        return success ? Result.success("租赁已终止") : Result.error("终止失败");
    }
    
    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }
}
