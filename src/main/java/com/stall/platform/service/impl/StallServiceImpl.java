package com.stall.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.Stall;
import com.stall.platform.entity.StallType;
import com.stall.platform.mapper.StallMapper;
import com.stall.platform.service.StallService;
import com.stall.platform.service.StallTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StallServiceImpl extends ServiceImpl<StallMapper, Stall> implements StallService {
    
    private final StallTypeService stallTypeService;
    
    public StallServiceImpl(StallTypeService stallTypeService) {
        this.stallTypeService = stallTypeService;
    }
    
    @Override
    public IPage<Stall> pageList(Integer pageNum, Integer pageSize, Long typeId, Integer status, String keyword) {
        LambdaQueryWrapper<Stall> wrapper = new LambdaQueryWrapper<>();
        if (typeId != null) {
            wrapper.eq(Stall::getTypeId, typeId);
        }
        if (status != null) {
            wrapper.eq(Stall::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Stall::getName, keyword)
                    .or().like(Stall::getStallNo, keyword)
                    .or().like(Stall::getLocation, keyword));
        }
        wrapper.orderByAsc(Stall::getStallNo);
        IPage<Stall> page = page(new Page<>(pageNum, pageSize), wrapper);
        // 填充类型名称
        page.getRecords().forEach(this::fillTypeName);
        return page;
    }
    
    @Override
    public List<Stall> listAvailable() {
        List<Stall> list = lambdaQuery().eq(Stall::getStatus, 0).list();
        list.forEach(this::fillTypeName);
        return list;
    }
    
    @Override
    public Stall getDetailById(Long id) {
        Stall stall = getById(id);
        if (stall != null) {
            fillTypeName(stall);
        }
        return stall;
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Stall stall = new Stall();
        stall.setId(id);
        stall.setStatus(status);
        return updateById(stall);
    }
    
    private void fillTypeName(Stall stall) {
        if (stall.getTypeId() != null) {
            StallType type = stallTypeService.getById(stall.getTypeId());
            if (type != null) {
                stall.setTypeName(type.getName());
            }
        }
    }
}
