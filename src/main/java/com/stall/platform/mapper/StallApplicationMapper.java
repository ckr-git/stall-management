package com.stall.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stall.platform.entity.StallApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StallApplicationMapper extends BaseMapper<StallApplication> {
    
    @Select("SELECT a.*, u.username, s.name as stall_name, s.stall_no " +
            "FROM stall_application a " +
            "LEFT JOIN user u ON a.user_id = u.id " +
            "LEFT JOIN stall s ON a.stall_id = s.id " +
            "WHERE a.deleted = 0 " +
            "AND (#{userId} IS NULL OR a.user_id = #{userId}) " +
            "AND (#{status} IS NULL OR a.status = #{status})")
    IPage<StallApplication> selectPageWithDetails(Page<StallApplication> page, 
                                                    @Param("userId") Long userId, 
                                                    @Param("status") Integer status);
}
