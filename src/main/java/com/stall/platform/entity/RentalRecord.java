package com.stall.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("rental_record")
public class RentalRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long applicationId;
    
    private Long userId;
    
    private Long stallId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal rentAmount;
    
    private BigDecimal deposit;
    
    private Integer paymentStatus; // 0-未支付 1-已支付
    
    private Integer status; // 1-租赁中 2-已到期 3-提前终止
    
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
