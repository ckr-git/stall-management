package com.stall.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stall.platform.entity.StallType;
import com.stall.platform.mapper.StallTypeMapper;
import com.stall.platform.service.StallTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StallTypeServiceImpl extends ServiceImpl<StallTypeMapper, StallType> implements StallTypeService {
    
    @Override
    public List<StallType> listAll() {
        return lambdaQuery().orderByAsc(StallType::getSortOrder).list();
    }
}
