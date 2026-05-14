FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -DskipTests package -pl ruoyi-admin -am

FROM eclipse-temurin:17-jre

WORKDIR /ruoyi/server
RUN mkdir -p /ruoyi/server/logs /ruoyi/server/temp

ENV SERVER_PORT=8080 \
    SNAIL_PORT=28080 \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    JAVA_OPTS=""

EXPOSE 8080 28080

COPY --from=build /workspace/ruoyi-admin/target/ruoyi-admin.jar ./app.jar

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${SERVER_PORT} -Dsnail-job.port=${SNAIL_PORT} -XX:+HeapDumpOnOutOfMemoryError ${JAVA_OPTS} -jar app.jar"]
