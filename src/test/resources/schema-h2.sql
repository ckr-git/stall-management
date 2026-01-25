-- H2数据库初始化脚本（测试用）
-- NON_KEYWORDS=USER已在连接字符串中配置

-- 用户表
CREATE TABLE IF NOT EXISTS user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50),
  phone VARCHAR(20),
  id_card VARCHAR(20),
  role VARCHAR(20) DEFAULT 'USER',
  status INT DEFAULT 1,
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 摊位类型表
CREATE TABLE IF NOT EXISTS stall_type (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255),
  sort_order INT DEFAULT 0,
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 摊位表
CREATE TABLE IF NOT EXISTS stall (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stall_no VARCHAR(50) UNIQUE,
  name VARCHAR(100) NOT NULL,
  type_id BIGINT,
  location VARCHAR(255),
  latitude DECIMAL(10,7),
  longitude DECIMAL(10,7),
  area DECIMAL(10,2),
  rent_price DECIMAL(10,2),
  status INT DEFAULT 0,
  description CLOB,
  image VARCHAR(500),
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 摊位申请表
CREATE TABLE IF NOT EXISTS stall_application (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  application_no VARCHAR(50) UNIQUE NOT NULL,
  user_id BIGINT NOT NULL,
  stall_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  business_type VARCHAR(100),
  business_license VARCHAR(255),
  reason CLOB,
  status INT DEFAULT 0,
  review_opinion CLOB,
  reviewer_id BIGINT,
  review_time TIMESTAMP,
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 卫生检查表
CREATE TABLE IF NOT EXISTS hygiene_inspection (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stall_id BIGINT NOT NULL,
  inspector_id BIGINT NOT NULL,
  inspection_date DATE NOT NULL,
  score INT,
  result VARCHAR(20),
  problems CLOB,
  suggestions CLOB,
  images VARCHAR(500),
  status INT DEFAULT 0,
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 反馈投诉表
CREATE TABLE IF NOT EXISTS feedback (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  stall_id BIGINT,
  type INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content CLOB NOT NULL,
  images VARCHAR(500),
  contact_phone VARCHAR(20),
  status INT DEFAULT 0,
  reply CLOB,
  handler_id BIGINT,
  handle_time TIMESTAMP,
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 公告表
CREATE TABLE IF NOT EXISTS announcement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content CLOB NOT NULL,
  type INT DEFAULT 1,
  priority INT DEFAULT 0,
  publisher_id BIGINT,
  status INT DEFAULT 1,
  publish_time TIMESTAMP,
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 租赁记录表
CREATE TABLE IF NOT EXISTS rental_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  application_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  stall_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  rent_amount DECIMAL(10,2),
  deposit DECIMAL(10,2),
  payment_status INT DEFAULT 0,
  status INT DEFAULT 1,
  deleted INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  username VARCHAR(50),
  operation VARCHAR(100),
  method VARCHAR(200),
  params CLOB,
  ip VARCHAR(50),
  location VARCHAR(100),
  time BIGINT,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
