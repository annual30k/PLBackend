# PatrolLink 后端框架来源

- 来源仓库：https://github.com/dromara/RuoYi-Vue-Plus
- 下载分支：5.X
- 下载方式：GitHub codeload ZIP
- 分支提交：6bfdcae06eaf218c4204382de277499be6c88c1b
- 下载日期：2026-05-14

## 选择原因

- Spring Boot 3.x：当前 `pom.xml` 使用 Spring Boot 3.5.14。
- 鉴权：已集成 Sa-Token Spring Boot 3 starter，可作为后台和 App 接口鉴权基础。
- 数据库：已集成 MyBatis-Plus Spring Boot 3 starter 和 dynamic-datasource，适合后续做 MySQL、达梦、人大金仓可插拔适配。
- Redis：已集成 Redisson，支持单机、哨兵和集群形态。
- 文件存储：框架文档明确采用 MinIO 分布式文件存储。
- 部署：自带 Dockerfile 与 docker-compose 脚本，便于后续改造成 PatrolLink 的 Docker 服务栈。

## PatrolLink 后续改造方向

- 将通用后台包名、菜单、Logo、登录页文案替换为 PatrolLink。
- 新增 `patrol-*` 业务模块：设备、指挥调度、警力上图、布控预警、媒体证据、SOS、流媒体会话。
- 增加达梦、人大金仓驱动与 SQL 方言适配脚本。
- 对接 PL2Android 已有 REST/WebSocket 契约。
- 增加高德地图、第三方人脸比对、第三方车牌 OCR 和 110 接处警预留接口。
