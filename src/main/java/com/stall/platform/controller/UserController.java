package com.stall.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stall.platform.common.Result;
import com.stall.platform.entity.User;
import com.stall.platform.security.LoginUser;
import com.stall.platform.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/profile")
    public Result<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            User user = userService.getById(loginUser.getUserId());
            if (user != null) {
                user.setPassword(null);
                return Result.success(user);
            }
        }
        return Result.error(401, "未登录");
    }
    
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            user.setId(loginUser.getUserId());
            user.setPassword(null); // 不允许通过此接口修改密码
            user.setRole(null); // 不允许修改角色
            boolean success = userService.updateById(user);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        }
        return Result.error(401, "未登录");
    }
    
    // ========== 管理员接口 ==========
    
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        IPage<User> page = userService.pageList(pageNum, pageSize, keyword, role);
        // 清除密码信息
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }
    
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }
    
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user.setPassword(null); // 不允许通过此接口修改密码
        boolean success = userService.updateById(user);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @PutMapping("/admin/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> resetPassword(@PathVariable Long id) {
        boolean success = userService.resetPassword(id);
        return success ? Result.success("密码已重置为123456") : Result.error("重置失败");
    }
    
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        boolean success = userService.updateById(user);
        return success ? Result.success("状态更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
