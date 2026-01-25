package com.stall.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stall")
public class Stall {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String stallNo;
    
    private String name;
    
    private Long typeId;
    
    private String location;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private BigDecimal area;
    
    private BigDecimal rentPrice;
    
    private Integer status; // 0-空闲 1-已租用 2-维护中
    
    private String description;
    
    private String image;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String typeName;
}
