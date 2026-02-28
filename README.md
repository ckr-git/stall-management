# 摊位管理平台

## 项目简介

基于 Spring Boot 3 + Vue 3 + Spring Security 开发的公园摊位管理系统，支持用户端和管理员端，提供摊位浏览、申请、审核、租赁、卫生管理、反馈投诉等完整业务闭环。

## 技术栈

### 后端
- **框架**: Spring Boot 3.1.5 / Spring Security 6 (JWT 无状态认证)
- **JDK**: 17+
- **ORM**: MyBatis-Plus 3.5.4.1
- **数据库**: MySQL 8.0
- **缓存**: Redis (可选)
- **工具**: Hutool 5.8.22 / Lombok / JJWT 0.11.5

### 前端
- **框架**: Vue 3.4 (Composition API + `<script setup>`)
- **语言**: TypeScript 5.4
- **构建**: Vite 5.2
- **UI**: Ant Design Vue 4.1.2
- **状态管理**: Pinia 2.1
- **HTTP**: Axios 1.6
- **图表**: ECharts 5.5 + vue-echarts 6.6
- **日期**: dayjs 1.11

## 功能模块

### 用户端
- **认证**: 注册 / 登录 / 登出 / 个人中心 / 修改密码
- **摊位浏览**: 列表 + 筛选 + 详情
- **申请管理**: 提交摊位申请 / 查看进度 / 取消申请
- **我的租赁**: 查看租赁记录 / 支付状态
- **反馈投诉**: 投诉 / 建议 / 咨询，查看处理结果
- **公告查看**: 系统公告 / 政策通知 / 活动通知

### 管理员端
- **仪表盘**: 统计数据 + 出租率趋势图 + 类型分布图 + 最近申请/反馈
- **用户管理**: 查看 / 编辑 / 启禁用 / 重置密码
- **摊位管理**: CRUD + 状态管理 + 类型管理
- **申请审核**: 审批流程 (通过时自动创建租赁记录)
- **租赁管理**: 支付状态更新 / 提前终止
- **卫生管理**: 检查记录 / 评分评级 / 整改跟踪
- **反馈处理**: 查看 / 回复 / 状态管理
- **公告发布**: CRUD + 优先级 + 发布/下架

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+
- Redis 6.0+ (可选)

### 1. 导入数据库
```bash
mysql -u root -p < database.sql
```

### 2. 修改后端配置
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/stall_platform
    username: root
    password: your_password
```

### 3. 启动后端
```bash
mvn spring-boot:run
# 后端 API: http://localhost:8080/api
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
# 前端: http://localhost:5173
```

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 用户 | user | user123 |

## 项目结构

```
stall-management/
├── src/main/java/com/stall/platform/
│   ├── common/          # Result 统一返回 + 全局异常处理
│   ├── config/          # MyBatis-Plus / CORS 配置
│   ├── security/        # SecurityConfig / JWT 过滤器 / JwtUtil
│   ├── controller/      # 9 个控制器 (Auth/User/Stall/StallType/Application/Rental/Hygiene/Feedback/Announcement)
│   ├── entity/          # 9 个实体类
│   ├── mapper/          # MyBatis-Plus Mapper
│   └── service/         # 服务接口 + 实现
├── src/test/            # 单元测试 + 集成测试
├── src/main/resources/
│   └── application.yml
├── frontend/
│   ├── src/
│   │   ├── api/         # API 层 (Axios)
│   │   ├── components/  # AdminLayout / UserLayout
│   │   ├── router/      # 路由 + 权限守卫
│   │   ├── stores/      # Pinia 用户状态
│   │   ├── utils/       # 日期格式化 / 本地存储
│   │   ├── views/admin/ # 10 个管理页面
│   │   └── views/user/  # 12 个用户页面
│   ├── package.json
│   └── vite.config.ts   # 代理 /api → localhost:8080
├── database.sql         # 数据库脚本 (9 张表)
└── pom.xml
```

## 数据库表结构

| 表名 | 说明 |
|------|------|
| user | 用户 (USER/ADMIN 角色，BCrypt 加密) |
| stall_type | 摊位类型 |
| stall | 摊位 (状态: 空闲/已租/维护) |
| stall_application | 申请 (雪花算法编号) |
| rental_record | 租赁记录 |
| hygiene_inspection | 卫生检查 |
| feedback | 反馈投诉 |
| announcement | 公告 |
| system_log | 系统日志 |

所有表使用逻辑删除 (`deleted`) 和自动填充时间戳 (`create_time` / `update_time`)。

## API 接口

### 公开接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录 |
| POST | /api/auth/register | 注册 |
| GET | /api/stall/list | 摊位列表 |
| GET | /api/stall/{id} | 摊位详情 |
| GET | /api/announcement/list | 公告列表 |
| GET | /api/announcement/{id} | 公告详情 |
| GET | /api/stall-type/list | 摊位类型列表 |

### 认证接口 (需 JWT Token)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/auth/info | 当前用户信息 |
| PUT | /api/auth/password | 修改密码 |
| POST | /api/application/submit | 提交申请 |
| GET | /api/application/my | 我的申请 |
| GET | /api/rental/my | 我的租赁 |
| POST | /api/feedback/submit | 提交反馈 |
| GET | /api/feedback/my | 我的反馈 |

### 管理员接口 (需 ADMIN 角色)
所有管理员接口路径匹配 `/api/*/admin/**`，如:
- `/api/user/admin/list` — 用户列表
- `/api/stall/admin` — 创建摊位
- `/api/application/admin/{id}/review` — 审核申请
- `/api/announcement/admin` — 发布公告

## 安全配置

- JWT 无状态认证，Token 有效期 7 天
- BCrypt 密码加密
- 路由级权限: `/*/admin/**` 需要 ADMIN 角色
- 方法级权限: `@PreAuthorize("hasRole('ADMIN')")`
- 前端路由守卫: 异步校验用户角色，防止缓存失效绕过

## 许可证

MIT License
