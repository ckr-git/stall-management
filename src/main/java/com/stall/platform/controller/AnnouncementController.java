package com.stall.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stall.platform.common.Result;
import com.stall.platform.entity.Announcement;
import com.stall.platform.security.LoginUser;
import com.stall.platform.service.AnnouncementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }
    
    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Integer type) {
        return Result.success(announcementService.listPublished(type));
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getDetailById(id);
        if (announcement != null) {
            return Result.success(announcement);
        }
        return Result.error("公告不存在");
    }
    
    // ========== 管理员接口 ==========
    
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        IPage<Announcement> page = announcementService.pageList(pageNum, pageSize, type, status);
        return Result.success(page);
    }
    
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> publish(@RequestBody Announcement announcement) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        announcement.setPublisherId(loginUser.getUserId());
        boolean success = announcementService.publish(announcement);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }
    
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        boolean success = announcementService.updateById(announcement);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = announcementService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = announcementService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }
}
