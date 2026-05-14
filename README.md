# PatrolLink Backend

[中文文档](README.zh-CN.md)

PatrolLink Backend is the server-side application for the smart law-enforcement headset command platform. It is based on Spring Boot 3, Java 17, Maven, MyBatis-Plus, Sa-Token, Redis/Redisson, and the RuoYi-Vue-Plus backend foundation.

## Overview

This repository contains the backend services for PatrolLink. It provides authentication, permission management, system administration, patrol business APIs, and the foundation for device status, dispatch, alerts, SOS events, media evidence, and operational monitoring.

The project is adapted from RuoYi-Vue-Plus and keeps the existing multi-tenant admin, RBAC, data dictionary, file storage, job scheduling, monitoring, workflow, and code generation modules. PatrolLink-specific API entry points are located under `ruoyi-admin/src/main/java/org/dromara/patrol`.

## Main Features

- Spring Boot 3 backend application
- Sa-Token based authentication and authorization
- MyBatis-Plus and dynamic data source support
- Redis/Redisson integration
- API encryption support for frontend-backend communication
- Multi-tenant system administration modules
- PatrolLink API controllers for management console and app-side integration
- Docker build and runtime image

## Tech Stack

- Java 17
- Maven 3.9+
- Spring Boot 3.5
- MyBatis-Plus
- Sa-Token
- Redis and Redisson
- MySQL by default, with extension points for other relational databases

## Requirements

- JDK 17+
- Maven 3.9+
- MySQL 8 or compatible database
- Redis 6+

The default development profile expects:

```text
MySQL: localhost:3306, database ry-vue, username root, password root
Redis: localhost:6379, password ruoyi123
Backend HTTP port: 8080
```

Adjust these values in:

```text
ruoyi-admin/src/main/resources/application-dev.yml
```

## Database Initialization

Import the SQL files under `script/sql` according to the database type. For the default MySQL setup, start with:

```text
script/sql/ry_vue_5.X.sql
script/sql/ry_job.sql
script/sql/ry_workflow.sql
```

## Local Development

```bash
mvn -DskipTests package -pl ruoyi-admin -am
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

The backend starts on:

```text
http://localhost:8080
```

## Docker

```bash
docker build -t patrollink-backend .
docker run --rm -p 8080:8080 -p 28080:28080 patrollink-backend
```

The image builds the `ruoyi-admin` module and runs `ruoyi-admin.jar`.

## Project Structure

```text
ruoyi-admin        Main application, web controllers, PatrolLink API controllers
ruoyi-common       Shared common modules
ruoyi-extend       Extended services such as monitor and job server
ruoyi-modules      System, generator, job, and workflow modules
script             SQL files, Docker Compose files, and helper scripts
```

## Related Repositories

- Frontend: https://github.com/annual30k/PLFront.git
- Android client: located in the parent PatrolLink workspace

## Framework Source

This project is adapted from the RuoYi-Vue-Plus backend framework. Source details are recorded in `PATROLLINK_SOURCE.md`.
