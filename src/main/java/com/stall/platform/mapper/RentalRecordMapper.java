package com.stall.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stall.platform.entity.RentalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RentalRecordMapper extends BaseMapper<RentalRecord> {
    
    @Select("SELECT r.*, u.username, s.name as stall_name, s.stall_no " +
            "FROM rental_record r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "LEFT JOIN stall s ON r.stall_id = s.id " +
            "WHERE r.deleted = 0 " +
            "AND (#{userId} IS NULL OR r.user_id = #{userId}) " +
            "AND (#{stallId} IS NULL OR r.stall_id = #{stallId})")
    IPage<RentalRecord> selectPageWithDetails(Page<RentalRecord> page, 
                                                @Param("userId") Long userId, 
                                                @Param("stallId") Long stallId);
}
