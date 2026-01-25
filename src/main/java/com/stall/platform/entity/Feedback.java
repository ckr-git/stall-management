package com.stall.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long stallId;
    
    private Integer type; // 1-投诉 2-建议 3-咨询
    
    private String title;
    
    private String content;
    
    private String images;
    
    private String contactPhone;
    
    private Integer status; // 0-待处理 1-处理中 2-已处理 3-已关闭
    
    private String reply;
    
    private Long handlerId;
    
    private LocalDateTime handleTime;
    
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
    private String handlerName;
}
