package com.stall.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hygiene_inspection")
public class HygieneInspection {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long stallId;
    
    private Long inspectorId;
    
    private LocalDate inspectionDate;
    
    private Integer score; // 0-100
    
    private String result; // 优秀、良好、合格、不合格
    
    private String problems;
    
    private String suggestions;
    
    private String images;
    
    private Integer status; // 0-待整改 1-已整改 2-无需整改
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String stallName;
    
    @TableField(exist = false)
    private String stallNo;
    
    @TableField(exist = false)
    private String inspectorName;
}
