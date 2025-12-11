# TODO List API
基于 Spring Boot 3.2 + JDK17 开发的轻量级待办事项（TODO）管理系统 API，提供用户认证、标签管理、TODO 全生命周期管理等核心功能，支持 JWT 认证、参数校验、统一响应格式，配套完整的 Swagger 接口文档。

## 一、项目介绍
该项目是一套面向个人/小团队的 TODO 管理后端 API，核心目标是提供高效、可扩展的待办事项管理能力，具备以下特性：
- 安全认证：基于 Spring Security + JWT 实现无状态用户认证，支持密码加密存储（BCrypt）；
-  核心功能：用户注册/登录、标签自定义、TODO 创建/更新/查询/状态管理；
- 规范设计：统一响应格式、全局异常处理、错误码枚举、参数校验；
- 文档支持：集成 SpringDoc OpenAPI 生成 Swagger 接口文档，支持在线调试；
- 易扩展：分层架构（Controller/Service/Repository），便于功能扩展和维护。

## 二、技术栈
| 技术/框架                | 版本/说明                     |
|--------------------------|------------------------------|
| JDK                      | 17（核心运行环境）           |
| Spring Boot              | 3.2.x（核心框架）            |
| Spring Security          | 6.x（安全认证）              |
| Spring Data JPA          | 3.2.x（数据访问层）          |
| PostgreSQL               | 14+（关系型数据库）          |
| JWT（JJWT）              | 0.11.5（令牌认证）           |
| Lombok                   | 1.8.30（简化代码）           |
| SpringDoc OpenAPI        | 2.2.0（Swagger 接口文档）    |
| Maven                    | 3.6+（项目构建工具）         |

## 三、快速开始
### 1. 环境要求
- 本地安装 JDK 17（配置 `JAVA_HOME`）；
- 本地安装 PostgreSQL 14+（创建数据库 `todolist`）；
- 安装 Maven 3.6+ 或使用 IDEA 内置 Maven。

### 2. 本地部署
#### 步骤1：克隆项目（示例）
```bash
git clone <你的项目仓库地址>
cd todo-list-api
```

#### 步骤2：配置数据库
修改 `src/main/resources/application.yml`（或 `application.properties`）：
```yaml
spring:
  # 数据库配置
  datasource:
    url: jdbc:postgresql://localhost:5432/todo_db
    username: postgres # 替换为你的PostgreSQL用户名
    password: 123456 # 替换为你的PostgreSQL密码
    driver-class-name: org.postgresql.Driver
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: update # 开发环境自动更新表结构（生产环境建议改为none）
    show-sql: true # 显示SQL日志
    properties:
      hibernate:
        format_sql: true # 格式化SQL日志
        dialect: org.hibernate.dialect.PostgreSQLDialect
# JWT配置
jwt:
  secret: my-secret-key-12345678901234567890123456789012 # 生产环境替换为复杂密钥
  expiration: 3600000 # Token过期时间（1小时，单位：毫秒）
```

#### 步骤3：构建并启动项目
```bash
# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run
# 或直接在IDEA中运行 TodoApplication.java
```

项目启动成功后，访问 `http://localhost:8080` 即可调用接口。

## 四、核心功能模块
### 1. 用户模块（User）
| 接口                | 方法 | 功能           | 权限要求 |
|---------------------|------|----------------|----------|
| `/api/users/register` | POST | 用户注册       | 匿名访问 |
| `/api/users/login`    | POST | 用户登录（获取JWT Token） | 匿名访问 |

**核心特性**：
- 用户名/邮箱唯一性校验；
- 密码 BCrypt 加密存储，不暴露明文；
- 登录返回 JWT Token，用于后续接口认证。

### 2. 标签模块（Tag）
| 接口                     | 方法 | 功能               | 权限要求 |
|--------------------------|------|--------------------|----------|
| `/api/tags/create`       | POST | 创建用户私有标签   | 已登录   |
| `/api/tags/user/{userId}`| GET  | 查询用户所有标签   | 已登录   |

**核心特性**：
- 标签属于用户私有资源，不跨用户访问；
- 同一用户标签名唯一校验。

### 3. TODO 模块（Todo）
| 接口                          | 方法 | 功能               | 权限要求 |
|-------------------------------|------|--------------------|----------|
| `/api/todos/createTodo`       | POST | 创建TODO事项       | 已登录   |
| `/api/todos/update/{todoId}`  | PUT  | 更新TODO（名称/状态/标签等） | 已登录（仅创建者） |
| `/api/todos/user/{userId}`    | GET  | 查询用户所有TODO   | 已登录   |

**核心特性**：
- 支持 TODO 状态（未开始/进行中/已完成）、优先级（低/中/高）管理；
- 支持关联多个用户标签；
- 仅 TODO 创建者可修改，权限隔离。

## 五、接口文档
项目集成 SpringDoc OpenAPI 生成 Swagger 文档，启动项目后访问：
```
http://localhost:8080/swagger-ui/index.html
```

### 文档使用说明：
1. 匿名接口（如注册/登录）可直接调用测试；
2. 需认证的接口：点击页面右上角「Authorize」，输入 `Bearer + 空格 + JWT Token`（如 `Bearer eyJhbGciOiJIUzI1NiJ9...`），即可调用


## 六、许可证
本项目为学习/开源用途。

---
### 扩展说明
- 生产环境建议：关闭 `jpa.ddl-auto=update`，使用其他安全工具管理数据库表结构；
- 性能优化：可添加 Redis 缓存高频查询（如用户标签、TODO 列表）；
- 功能扩展：可新增 TODO 分享、截止时间提醒、团队协作等功能。
