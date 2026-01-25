package com.stall.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.HygieneInspection;
import com.stall.platform.entity.Stall;
import com.stall.platform.entity.User;
import com.stall.platform.mapper.HygieneInspectionMapper;
import com.stall.platform.service.HygieneInspectionService;
import com.stall.platform.service.StallService;
import com.stall.platform.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HygieneInspectionServiceImpl extends ServiceImpl<HygieneInspectionMapper, HygieneInspection> 
        implements HygieneInspectionService {
    
    private final StallService stallService;
    private final UserService userService;
    
    public HygieneInspectionServiceImpl(StallService stallService, UserService userService) {
        this.stallService = stallService;
        this.userService = userService;
    }
    
    @Override
    public IPage<HygieneInspection> pageList(Integer pageNum, Integer pageSize, Long stallId, String result) {
        LambdaQueryWrapper<HygieneInspection> wrapper = new LambdaQueryWrapper<>();
        if (stallId != null) {
            wrapper.eq(HygieneInspection::getStallId, stallId);
        }
        if (StringUtils.hasText(result)) {
            wrapper.eq(HygieneInspection::getResult, result);
        }
        wrapper.orderByDesc(HygieneInspection::getInspectionDate);
        IPage<HygieneInspection> page = page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillDetails);
        return page;
    }
    
    @Override
    public boolean addInspection(HygieneInspection inspection) {
        // 根据分数设置结果
        int score = inspection.getScore();
        if (score >= 90) {
            inspection.setResult("优秀");
            inspection.setStatus(2); // 无需整改
        } else if (score >= 80) {
            inspection.setResult("良好");
            inspection.setStatus(2);
        } else if (score >= 60) {
            inspection.setResult("合格");
            inspection.setStatus(StringUtils.hasText(inspection.getProblems()) ? 0 : 2);
        } else {
            inspection.setResult("不合格");
            inspection.setStatus(0); // 待整改
        }
        return save(inspection);
    }
    
    @Override
    public boolean updateRectification(Long id, Integer status) {
        HygieneInspection inspection = new HygieneInspection();
        inspection.setId(id);
        inspection.setStatus(status);
        return updateById(inspection);
    }
    
    @Override
    public HygieneInspection getDetailById(Long id) {
        HygieneInspection inspection = getById(id);
        if (inspection != null) {
            fillDetails(inspection);
        }
        return inspection;
    }
    
    private void fillDetails(HygieneInspection inspection) {
        if (inspection.getStallId() != null) {
            Stall stall = stallService.getById(inspection.getStallId());
            if (stall != null) {
                inspection.setStallName(stall.getName());
                inspection.setStallNo(stall.getStallNo());
            }
        }
        if (inspection.getInspectorId() != null) {
            User user = userService.getById(inspection.getInspectorId());
            if (user != null) {
                inspection.setInspectorName(user.getNickname());
            }
        }
    }
}
