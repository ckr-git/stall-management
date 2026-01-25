package com.stall.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("stall_application")
public class StallApplication {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String applicationNo;
    
    private Long userId;
    
    private Long stallId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String businessType;
    
    private String businessLicense;
    
    private String reason;
    
    private Integer status; // 0-待审核 1-审核通过 2-审核拒绝 3-已取消
    
    private String reviewOpinion;
    
    private Long reviewerId;
    
    private LocalDateTime reviewTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String stallName;
    
    @TableField(exist = false)
    private String stallNo;
}
