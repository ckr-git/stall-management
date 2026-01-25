package com.stall.platform.controller;

import com.stall.platform.common.Result;
import com.stall.platform.entity.StallType;
import com.stall.platform.service.StallTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stall-type")
public class StallTypeController {
    
    private final StallTypeService stallTypeService;
    
    public StallTypeController(StallTypeService stallTypeService) {
        this.stallTypeService = stallTypeService;
    }
    
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(stallTypeService.listAll());
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        StallType stallType = stallTypeService.getById(id);
        if (stallType != null) {
            return Result.success(stallType);
        }
        return Result.error("摊位类型不存在");
    }
    
    // ========== 管理员接口 ==========
    
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> add(@RequestBody StallType stallType) {
        boolean success = stallTypeService.save(stallType);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody StallType stallType) {
        stallType.setId(id);
        boolean success = stallTypeService.updateById(stallType);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = stallTypeService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
