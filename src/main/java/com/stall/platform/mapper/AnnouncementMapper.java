package com.stall.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stall.platform.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
