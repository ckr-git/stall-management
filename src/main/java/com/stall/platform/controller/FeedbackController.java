package com.stall.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stall.platform.common.Result;
import com.stall.platform.entity.Feedback;
import com.stall.platform.security.LoginUser;
import com.stall.platform.service.FeedbackService;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    @PostMapping("/submit")
    public Result<?> submit(@RequestBody Feedback feedback) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        feedback.setUserId(loginUser.getUserId());
        boolean success = feedbackService.submit(feedback);
        return success ? Result.success("反馈提交成功") : Result.error("提交失败");
    }
    
    @GetMapping("/my")
    public Result<?> myList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        IPage<Feedback> page = feedbackService.pageList(pageNum, pageSize, 
                loginUser.getUserId(), type, status);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Feedback feedback = feedbackService.getDetailById(id);
        if (feedback != null) {
            return Result.success(feedback);
        }
        return Result.error("反馈不存在");
    }
    
    // ========== 管理员接口 ==========
    
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        IPage<Feedback> page = feedbackService.pageListForAdmin(pageNum, pageSize, type, status);
        return Result.success(page);
    }
    
    @PutMapping("/admin/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> reply(@PathVariable Long id, @RequestBody ReplyDTO replyDTO) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        boolean success = feedbackService.reply(id, replyDTO.getReply(), loginUser.getUserId());
        return success ? Result.success("回复成功") : Result.error("回复失败");
    }
    
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = feedbackService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("更新失败");
    }
    
    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }
    
    @Data
    public static class ReplyDTO {
        private String reply;
    }
}
