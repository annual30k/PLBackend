# PatrolLink 数据库兼容说明

## 脚本选择

- MySQL 8：执行 `script/sql/ry_vue_5.X.sql`、`ry_job.sql`、`ry_workflow.sql`，再执行 `script/sql/patrol/05-patrol.sql`。
- KingbaseES V8：先执行 RuoYi 的 `script/sql/postgres/*.sql`，再执行 `script/sql/patrol/kingbase/05-patrol-kingbase.sql`。
- 达梦 DM8：先执行 RuoYi 的 `script/sql/oracle/*.sql`，再执行 `script/sql/patrol/dm/05-patrol-dm.sql`。

## 接入方式

后端通过 dynamic-datasource 保持数据库可插拔。生产环境只需要替换以下环境变量，不改业务代码：

```bash
SPRING_DATASOURCE_DYNAMIC_DATASOURCE_MASTER_DRIVER_CLASS_NAME=目标数据库驱动
SPRING_DATASOURCE_DYNAMIC_DATASOURCE_MASTER_URL=目标 JDBC URL
SPRING_DATASOURCE_DYNAMIC_DATASOURCE_MASTER_USERNAME=用户名
SPRING_DATASOURCE_DYNAMIC_DATASOURCE_MASTER_PASSWORD=密码
```

Kingbase/达梦 JDBC 驱动通常由厂商随授权包提供，未直接放入仓库。上线镜像可通过企业 Maven 私服或 Dockerfile 扩展层加入驱动 jar。

## 当前验证状态

- MySQL：已通过本地 Docker Compose 启动与业务接口冒烟。
- KingbaseES：业务 DDL 已通过 `wephoon/kingbase:latest` 临时容器真库验证，创建 20 张 `patrol_*` 表。该社区镜像默认 entrypoint 会因日志文件问题退出，验证时使用手动启动并临时调整本地认证为 trust。
- 达梦：业务 DDL 使用 DM/Oracle 风格维护；Docker Hub 存在 `forresttse/dm8`、`sizx/dm8` 等社区镜像，但本机拉取时大层下载长时间无进展，尚未完成真库执行。需要在可稳定拉取镜像或客户授权 DM8 环境继续验证。
