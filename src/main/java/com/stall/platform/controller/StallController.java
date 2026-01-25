package com.stall.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stall.platform.common.Result;
import com.stall.platform.entity.Stall;
import com.stall.platform.service.StallService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stall")
public class StallController {
    
    private final StallService stallService;
    
    public StallController(StallService stallService) {
        this.stallService = stallService;
    }
    
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        IPage<Stall> page = stallService.pageList(pageNum, pageSize, typeId, status, keyword);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Stall stall = stallService.getDetailById(id);
        if (stall != null) {
            return Result.success(stall);
        }
        return Result.error("摊位不存在");
    }
    
    @GetMapping("/available")
    public Result<?> listAvailable() {
        return Result.success(stallService.listAvailable());
    }
    
    // ========== 管理员接口 ==========
    
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> add(@RequestBody Stall stall) {
        stall.setStatus(0); // 默认空闲状态
        boolean success = stallService.save(stall);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody Stall stall) {
        stall.setId(id);
        boolean success = stallService.updateById(stall);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = stallService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = stallService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
