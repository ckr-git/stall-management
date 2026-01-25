package com.stall.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stall.platform.common.Result;
import com.stall.platform.entity.StallApplication;
import com.stall.platform.security.LoginUser;
import com.stall.platform.service.StallApplicationService;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
public class ApplicationController {
    
    private final StallApplicationService applicationService;
    
    public ApplicationController(StallApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    @PostMapping("/submit")
    public Result<?> submit(@RequestBody StallApplication application) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        application.setUserId(loginUser.getUserId());
        boolean success = applicationService.submit(application);
        return success ? Result.success("申请提交成功") : Result.error("提交失败");
    }
    
    @GetMapping("/my")
    public Result<?> myList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        IPage<StallApplication> page = applicationService.pageList(pageNum, pageSize, 
                loginUser.getUserId(), status);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        StallApplication application = applicationService.getDetailById(id);
        if (application != null) {
            return Result.success(application);
        }
        return Result.error("申请不存在");
    }
    
    @DeleteMapping("/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        boolean success = applicationService.cancel(id, loginUser.getUserId());
        return success ? Result.success("取消成功") : Result.error("取消失败，申请可能已被处理");
    }
    
    // ========== 管理员接口 ==========
    
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        IPage<StallApplication> page = applicationService.pageListForAdmin(pageNum, pageSize, status);
        return Result.success(page);
    }
    
    @PutMapping("/admin/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> review(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        boolean success = applicationService.review(id, reviewDTO.getStatus(), 
                reviewDTO.getReviewOpinion(), loginUser.getUserId());
        return success ? Result.success("审核完成") : Result.error("审核失败");
    }
    
    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }
    
    @Data
    public static class ReviewDTO {
        private Integer status;
        private String reviewOpinion;
    }
}
