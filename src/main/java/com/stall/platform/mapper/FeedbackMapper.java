package com.stall.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stall.platform.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
    
    @Select("SELECT f.*, u.username, s.name as stall_name, h.nickname as handler_name " +
            "FROM feedback f " +
            "LEFT JOIN user u ON f.user_id = u.id " +
            "LEFT JOIN stall s ON f.stall_id = s.id " +
            "LEFT JOIN user h ON f.handler_id = h.id " +
            "WHERE f.deleted = 0 " +
            "AND (#{userId} IS NULL OR f.user_id = #{userId}) " +
            "AND (#{type} IS NULL OR f.type = #{type}) " +
            "AND (#{status} IS NULL OR f.status = #{status})")
    IPage<Feedback> selectPageWithDetails(Page<Feedback> page, 
                                           @Param("userId") Long userId,
                                           @Param("type") Integer type, 
                                           @Param("status") Integer status);
}
