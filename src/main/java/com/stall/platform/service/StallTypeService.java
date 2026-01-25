package com.stall.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stall.platform.entity.StallType;

import java.util.List;

public interface StallTypeService extends IService<StallType> {
    
    List<StallType> listAll();
}
