package com.stall.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stall.platform.entity.Stall;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StallMapper extends BaseMapper<Stall> {
    
    @Select("SELECT s.*, t.name as type_name FROM stall s " +
            "LEFT JOIN stall_type t ON s.type_id = t.id " +
            "WHERE s.deleted = 0 " +
            "AND (#{typeId} IS NULL OR s.type_id = #{typeId}) " +
            "AND (#{status} IS NULL OR s.status = #{status}) " +
            "AND (#{keyword} IS NULL OR s.name LIKE CONCAT('%', #{keyword}, '%') OR s.stall_no LIKE CONCAT('%', #{keyword}, '%'))")
    IPage<Stall> selectPageWithType(Page<Stall> page, @Param("typeId") Long typeId, 
                                     @Param("status") Integer status, @Param("keyword") String keyword);
}
