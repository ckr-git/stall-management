package com.stall.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stall.platform.entity.HygieneInspection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HygieneInspectionMapper extends BaseMapper<HygieneInspection> {
    
    @Select("SELECT h.*, s.name as stall_name, s.stall_no, u.nickname as inspector_name " +
            "FROM hygiene_inspection h " +
            "LEFT JOIN stall s ON h.stall_id = s.id " +
            "LEFT JOIN user u ON h.inspector_id = u.id " +
            "WHERE h.deleted = 0 " +
            "AND (#{stallId} IS NULL OR h.stall_id = #{stallId}) " +
            "AND (#{result} IS NULL OR h.result = #{result})")
    IPage<HygieneInspection> selectPageWithDetails(Page<HygieneInspection> page, 
                                                     @Param("stallId") Long stallId, 
                                                     @Param("result") String result);
}
