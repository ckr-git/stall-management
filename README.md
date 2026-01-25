# 摊位管理平台

## 项目简介

这是一个基于SpringBoot3 + Vue3 + SpringSecurity开发的公园摊位管理系统，支持摊位用户端和管理员端，提供完整的摊位浏览、申请、审核、卫生管理、反馈投诉等功能。

## 技术栈

### 后端技术
- **架构**: B/S、MVC
- **JDK版本**: JDK17+
- **框架**: SpringBoot 3.1.5
- **安全框架**: Spring Security + JWT
- **ORM**: MyBatis-Plus 3.5.4.1
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **工具类**: Hutool

### 前端技术
- **框架**: Vue 3
- **构建工具**: Vite
- **UI库**: Element Plus
- **HTTP客户端**: Axios
- **状态管理**: Pinia

## 功能模块

### 摊位用户端功能

#### 账号认证
- **登录注册**: 账号密码认证，记住账号功能
- **个人中心**: 管理个人资料、更改密码

#### 摊位浏览
- **查看所有摊位信息与位置**: 列表展示、地图展示、详情查看
- **摊位筛选**: 按类型、状态、位置等条件筛选

#### 申请管理
- **提交摊位申请**: 选择摊位、填写申请信息、上传营业执照
- **查看申请进度**: 实时查看申请状态（待审核、审核通过、审核拒绝）

#### 反馈投诉
- **提交问题反馈和建议**: 投诉、建议、咨询三种类型
- **查看处理结果**: 跟踪反馈处理进度

#### 公告查看
- **查看系统公告信息**: 系统公告、政策通知、活动通知

### 管理员端功能

#### 用户管理
- **用户信息维护**: 查看、编辑用户信息
- **重置密码**: 重置用户密码
- **账号增删改查**: 批量管理用户账号

#### 摊位管理
- **摊位信息管理**: 添加、编辑、删除摊位
- **位置规划**: 地图标注摊位位置
- **类型设置**: 摊位分类管理

#### 申请审核
- **摊位申请审批流程**: 审核申请、填写审核意见
- **状态更新**: 通过/拒绝申请、状态流转

#### 卫生管理
- **摊位卫生检查记录**: 录入检查结果、评分
- **卫生评级**: 优秀、良好、合格、不合格评级
- **整改跟踪**: 问题记录、整改建议、整改确认

#### 反馈处理
- **处理用户反馈与投诉**: 查看反馈、回复处理、关闭反馈

#### 公告发布
- **发布系统通知与公告**: 编辑公告内容、设置优先级、发布/下架

#### 个人中心
- **管理个人信息**: 修改资料、更改密码

## 系统亮点

1. ✅ **使用SpringBoot3+Vue3技术框架实现，前后端完全分离架构**
2. ✅ **使用SpringSecurity安全框架+JWT进行权限管理，并对用户密码进行加密**
3. ✅ **账号密码认证**: 支持记住账号功能
4. ✅ **摊位位置规划**: 支持地图展示和位置规划
5. ✅ **完整的申请审批流程**: 从申请到审核到租赁的完整流程
6. ✅ **卫生管理系统**: 检查记录、评分评级、整改跟踪
7. ✅ **反馈投诉处理**: 完整的反馈处理流程

## 项目结构

```
摊位管理平台/
├── src/
│   ├── main/
│   │   ├── java/com/stall/platform/
│   │   │   ├── StallPlatformApplication.java  # 主启动类
│   │   │   ├── common/                          # 通用类
│   │   │   │   ├── Result.java                 # 统一返回结果
│   │   │   │   └── ...                         # 其他工具类
│   │   │   ├── config/                          # 配置类
│   │   │   │   └── MyBatisPlusConfig.java      # MyBatis-Plus配置
│   │   │   ├── security/                        # Security配置
│   │   │   │   ├── SecurityConfig.java         # Security配置
│   │   │   │   ├── JwtUtil.java                # JWT工具类
│   │   │   │   ├── JwtAuthenticationFilter.java # JWT过滤器
│   │   │   │   └── UserDetailsServiceImpl.java  # 用户详情服务
│   │   │   ├── controller/                      # 控制器
│   │   │   │   ├── AuthController.java         # 认证控制器
│   │   │   │   ├── StallController.java        # 摊位控制器
│   │   │   │   ├── ApplicationController.java   # 申请控制器
│   │   │   │   └── ...                         # 其他控制器
│   │   │   ├── entity/                          # 实体类
│   │   │   │   ├── User.java                   # 用户实体
│   │   │   │   ├── Stall.java                  # 摊位实体
│   │   │   │   ├── StallApplication.java       # 申请实体
│   │   │   │   └── ...                         # 其他实体
│   │   │   ├── mapper/                          # Mapper接口
│   │   │   └── service/                         # 服务层
│   │   └── resources/
│   │       ├── application.yml                  # 配置文件
│   │       └── mapper/                          # MyBatis XML
│   └── test/                                     # 测试
├── frontend/                                     # Vue3前端项目
├── database.sql                                  # 数据库SQL脚本
├── pom.xml                                       # Maven配置
└── README.md                                     # 项目文档
```

## 数据库表结构

主要数据表（共9张）:
- `user` - 用户表（摊位用户和管理员）
- `stall_type` - 摊位类型表
- `stall` - 摊位表
- `stall_application` - 摊位申请表
- `hygiene_inspection` - 卫生检查表
- `feedback` - 反馈投诉表
- `announcement` - 公告表
- `rental_record` - 租赁记录表
- `system_log` - 系统日志表

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+（可选）
- Node.js 16+ (用于前端)

### 后端启动

1. **导入数据库**
```bash
mysql -u root -p < database.sql
```

2. **修改配置**
```yaml
# 修改src/main/resources/application.yml中的数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/stall_platform
    username: root
    password: your_password
```

3. **启动项目**
```bash
# 方式1: Maven命令
mvn spring-boot:run

# 方式2: IDEA直接运行StallPlatformApplication类
```

4. **访问接口**
```
后端API: http://localhost:8080/api
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问: http://localhost:5173

## 初始化数据

### 创建管理员账号
```sql
-- 在数据库中执行（密码使用BCrypt加密）
INSERT INTO `user` (username, password, nickname, phone, role, status) 
VALUES ('admin', '$2a$10$ENCRYPTED_PASSWORD', '系统管理员', '13800138000', 'ADMIN', 1);
```

### 创建摊位类型
```sql
INSERT INTO `stall_type` (name, description, sort_order) VALUES 
('餐饮摊位', '提供餐饮服务的摊位', 1),
('零售摊位', '销售商品的摊位', 2),
('娱乐摊位', '提供娱乐服务的摊位', 3),
('手工艺摊位', '手工艺品展示销售', 4);
```

## API接口文档

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 摊位接口
- `GET /api/stall/list` - 分页查询摊位列表
- `GET /api/stall/{id}` - 获取摊位详情
- `POST /api/stall` - 添加摊位（管理员）
- `PUT /api/stall/{id}` - 更新摊位（管理员）
- `DELETE /api/stall/{id}` - 删除摊位（管理员）

### 申请接口
- `POST /api/application/submit` - 提交申请
- `GET /api/application/my` - 查看我的申请
- `GET /api/application/{id}` - 获取申请详情
- `PUT /api/application/{id}/review` - 审核申请（管理员）
- `DELETE /api/application/{id}` - 取消申请

### 反馈接口
- `POST /api/feedback/submit` - 提交反馈
- `GET /api/feedback/my` - 查看我的反馈
- `PUT /api/feedback/{id}/reply` - 回复反馈（管理员）

### 公告接口
- `GET /api/announcement/list` - 查看公告列表
- `GET /api/announcement/{id}` - 获取公告详情
- `POST /api/announcement` - 发布公告（管理员）

## Security配置说明

### JWT认证流程
1. 用户登录，验证账号密码
2. 验证成功后生成JWT Token
3. 客户端在请求头中携带Token：`Authorization: Bearer {token}`
4. 后端通过JWT过滤器验证Token
5. 验证通过后获取用户信息，设置Security上下文

### 角色权限
- **USER**: 摊位用户，可以浏览摊位、提交申请、提交反馈
- **ADMIN**: 管理员，拥有所有权限

## 注意事项

1. 首次运行前请确保MySQL服务已启动
2. 数据库密码请根据实际情况修改
3. JWT密钥建议在生产环境中使用更复杂的字符串
4. SpringSecurity会对密码进行BCrypt加密，初始密码需要使用工具生成
5. 确保JDK版本为17或以上

## 默认测试账号

### 管理员
```
用户名: admin
密码: admin123
角色: ADMIN
```

### 摊位用户
```
用户名: user
密码: user123
角色: USER
```

## 后续优化方向

1. 添加支付功能（租金缴纳）
2. 添加地图功能（高德地图/百度地图集成）
3. 添加短信通知功能
4. 添加数据统计图表（租赁统计、收入统计）
5. 添加移动端支持
6. 优化前端页面交互体验
7. 添加单元测试和集成测试

## 技术支持

如有问题，欢迎提Issue或PR。

## 许可证

MIT License
