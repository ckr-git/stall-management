-- 摊位管理平台数据库

CREATE DATABASE IF NOT EXISTS stall_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE stall_platform;

-- 用户表（摊位用户和管理员）
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（账号）',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `phone` VARCHAR(20) COMMENT '手机号',
  `id_card` VARCHAR(20) COMMENT '身份证号',
  `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER-摊位用户 ADMIN-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_username (`username`),
  INDEX idx_phone (`phone`),
  INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 摊位类型表
CREATE TABLE `stall_type` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '类型ID',
  `name` VARCHAR(50) NOT NULL COMMENT '类型名称',
  `description` VARCHAR(255) COMMENT '类型描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='摊位类型表';

-- 摊位表
CREATE TABLE `stall` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '摊位ID',
  `stall_no` VARCHAR(50) UNIQUE COMMENT '摊位编号',
  `name` VARCHAR(100) NOT NULL COMMENT '摊位名称',
  `type_id` BIGINT COMMENT '摊位类型ID',
  `location` VARCHAR(255) COMMENT '位置描述',
  `latitude` DECIMAL(10,7) COMMENT '纬度',
  `longitude` DECIMAL(10,7) COMMENT '经度',
  `area` DECIMAL(10,2) COMMENT '面积（平方米）',
  `rent_price` DECIMAL(10,2) COMMENT '租金（元/月）',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-空闲 1-已租用 2-维护中',
  `description` TEXT COMMENT '摊位描述',
  `image` VARCHAR(500) COMMENT '摊位图片',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_stall_no (`stall_no`),
  INDEX idx_type (`type_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='摊位表';

-- 摊位申请表
CREATE TABLE `stall_application` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
  `application_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '申请编号',
  `user_id` BIGINT NOT NULL COMMENT '申请人ID',
  `stall_id` BIGINT NOT NULL COMMENT '摊位ID',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `business_type` VARCHAR(100) COMMENT '经营类型',
  `business_license` VARCHAR(255) COMMENT '营业执照',
  `reason` TEXT COMMENT '申请理由',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-审核通过 2-审核拒绝 3-已取消',
  `review_opinion` TEXT COMMENT '审核意见',
  `reviewer_id` BIGINT COMMENT '审核人ID',
  `review_time` DATETIME COMMENT '审核时间',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_application_no (`application_no`),
  INDEX idx_user (`user_id`),
  INDEX idx_stall (`stall_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='摊位申请表';

-- 卫生检查表
CREATE TABLE `hygiene_inspection` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '检查ID',
  `stall_id` BIGINT NOT NULL COMMENT '摊位ID',
  `inspector_id` BIGINT NOT NULL COMMENT '检查人ID',
  `inspection_date` DATE NOT NULL COMMENT '检查日期',
  `score` INT COMMENT '评分（0-100）',
  `result` VARCHAR(20) COMMENT '结果：优秀、良好、合格、不合格',
  `problems` TEXT COMMENT '存在问题',
  `suggestions` TEXT COMMENT '整改建议',
  `images` VARCHAR(500) COMMENT '检查照片',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待整改 1-已整改 2-无需整改',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_stall (`stall_id`),
  INDEX idx_inspector (`inspector_id`),
  INDEX idx_date (`inspection_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卫生检查表';

-- 反馈投诉表
CREATE TABLE `feedback` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `stall_id` BIGINT COMMENT '相关摊位ID',
  `type` TINYINT NOT NULL COMMENT '类型：1-投诉 2-建议 3-咨询',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `images` VARCHAR(500) COMMENT '图片',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已处理 3-已关闭',
  `reply` TEXT COMMENT '回复内容',
  `handler_id` BIGINT COMMENT '处理人ID',
  `handle_time` DATETIME COMMENT '处理时间',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user (`user_id`),
  INDEX idx_stall (`stall_id`),
  INDEX idx_type (`type`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈投诉表';

-- 公告表
CREATE TABLE `announcement` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `type` TINYINT DEFAULT 1 COMMENT '类型：1-系统公告 2-政策通知 3-活动通知',
  `priority` TINYINT DEFAULT 0 COMMENT '优先级：0-普通 1-重要 2-紧急',
  `publisher_id` BIGINT COMMENT '发布人ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已下架',
  `publish_time` DATETIME COMMENT '发布时间',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_type (`type`),
  INDEX idx_status (`status`),
  INDEX idx_publish_time (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 租赁记录表
CREATE TABLE `rental_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `application_id` BIGINT NOT NULL COMMENT '申请ID',
  `user_id` BIGINT NOT NULL COMMENT '租户ID',
  `stall_id` BIGINT NOT NULL COMMENT '摊位ID',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `rent_amount` DECIMAL(10,2) COMMENT '租金总额',
  `deposit` DECIMAL(10,2) COMMENT '押金',
  `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付 1-已支付',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-租赁中 2-已到期 3-提前终止',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_application (`application_id`),
  INDEX idx_user (`user_id`),
  INDEX idx_stall (`stall_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁记录表';

-- 系统日志表
CREATE TABLE `system_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT COMMENT '用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `operation` VARCHAR(100) COMMENT '操作',
  `method` VARCHAR(200) COMMENT '请求方法',
  `params` TEXT COMMENT '请求参数',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `location` VARCHAR(100) COMMENT '操作地点',
  `time` BIGINT COMMENT '耗时（毫秒）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (`user_id`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';
