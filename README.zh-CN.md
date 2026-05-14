# PatrolLink 后端

[English](README.md)

PatrolLink Backend 是智能执法耳机指挥平台的后端服务。项目基于 Spring Boot 3、Java 17、Maven、MyBatis-Plus、Sa-Token、Redis/Redisson 以及 RuoYi-Vue-Plus 后端基础框架改造。

## 项目简介

本仓库提供 PatrolLink 的后端服务，包括登录鉴权、权限管理、系统管理、巡防业务接口，并为设备状态、指挥调度、预警、SOS、媒体证据和运行监控等业务提供后端基础能力。

项目保留了 RuoYi-Vue-Plus 的多租户后台、RBAC、字典、文件存储、任务调度、监控、工作流和代码生成等模块。PatrolLink 业务接口入口位于 `ruoyi-admin/src/main/java/org/dromara/patrol`。

## 主要功能

- Spring Boot 3 后端应用
- 基于 Sa-Token 的登录认证与权限控制
- MyBatis-Plus 与动态数据源支持
- Redis/Redisson 集成
- 前后端接口加密支持
- 多租户系统管理模块
- 面向管理端和 App 端的 PatrolLink API 控制器
- Docker 构建与运行镜像

## 技术栈

- Java 17
- Maven 3.9+
- Spring Boot 3.5
- MyBatis-Plus
- Sa-Token
- Redis 和 Redisson
- 默认使用 MySQL，并预留其他关系型数据库适配空间

## 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8 或兼容数据库
- Redis 6+

默认开发环境配置为：

```text
MySQL: localhost:3306, database ry-vue, username root, password root
Redis: localhost:6379, password ruoyi123
后端 HTTP 端口: 8080
```

如需调整，请修改：

```text
ruoyi-admin/src/main/resources/application-dev.yml
```

## 数据库初始化

根据数据库类型导入 `script/sql` 下的 SQL 文件。默认 MySQL 环境可先导入：

```text
script/sql/ry_vue_5.X.sql
script/sql/ry_job.sql
script/sql/ry_workflow.sql
```

## 本地开发

```bash
mvn -DskipTests package -pl ruoyi-admin -am
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

服务默认启动地址：

```text
http://localhost:8080
```

## Docker 部署

```bash
docker build -t patrollink-backend .
docker run --rm -p 8080:8080 -p 28080:28080 patrollink-backend
```

镜像会构建 `ruoyi-admin` 模块并运行 `ruoyi-admin.jar`。

## 目录结构

```text
ruoyi-admin        主应用、Web 控制器、PatrolLink API 控制器
ruoyi-common       公共能力模块
ruoyi-extend       监控、任务服务等扩展模块
ruoyi-modules      系统、代码生成、任务、工作流等业务模块
script             SQL、Docker Compose 和辅助脚本
```

## 相关仓库

- 前端仓库：https://github.com/annual30k/PLFront.git
- Android 客户端：位于 PatrolLink 父级工作区

## 框架来源

本项目基于 RuoYi-Vue-Plus 后端框架改造。来源信息记录在 `PATROLLINK_SOURCE.md`。

原始 RuoYi-Vue-Plus 后端 README 已保留在 `README.ruoyi.md`。
