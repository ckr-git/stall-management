package com.stall.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stall.platform.common.Result;
import com.stall.platform.entity.HygieneInspection;
import com.stall.platform.security.LoginUser;
import com.stall.platform.service.HygieneInspectionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hygiene")
public class HygieneInspectionController {
    
    private final HygieneInspectionService hygieneInspectionService;
    
    public HygieneInspectionController(HygieneInspectionService hygieneInspectionService) {
        this.hygieneInspectionService = hygieneInspectionService;
    }
    
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long stallId,
            @RequestParam(required = false) String result) {
        IPage<HygieneInspection> page = hygieneInspectionService.pageList(pageNum, pageSize, stallId, result);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        HygieneInspection inspection = hygieneInspectionService.getDetailById(id);
        if (inspection != null) {
            return Result.success(inspection);
        }
        return Result.error("检查记录不存在");
    }
    
    // ========== 管理员接口 ==========
    
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> add(@RequestBody HygieneInspection inspection) {
        LoginUser loginUser = getCurrentUser();
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        inspection.setInspectorId(loginUser.getUserId());
        boolean success = hygieneInspectionService.addInspection(inspection);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody HygieneInspection inspection) {
        inspection.setId(id);
        boolean success = hygieneInspectionService.updateById(inspection);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @PutMapping("/admin/{id}/rectification")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateRectification(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = hygieneInspectionService.updateRectification(id, status);
        return success ? Result.success("状态更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = hygieneInspectionService.removeById(id);
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
