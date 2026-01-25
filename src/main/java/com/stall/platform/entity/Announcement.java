package com.stall.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class Announcement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    private Integer type; // 1-系统公告 2-政策通知 3-活动通知
    
    private Integer priority; // 0-普通 1-重要 2-紧急
    
    private Long publisherId;
    
    private Integer status; // 0-草稿 1-已发布 2-已下架
    
    private LocalDateTime publishTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String publisherName;
}
