
FROM eclipse-temurin:11-jdk-alpine as build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:11-jdk-alpine

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app


ENV SKT_MS_MINIO_PORT 8568

ENV LOGS_URI localhost:5000
ENV CONFIG_SERVER_URL http://68.183.104.65:9090
ENV SERVER_PROFILE test

ENV URL_KEYCLOAK localhost:8080
ENV REALM_KEYCLOAK skitter
ENV RESOURCE_KEYCLOAK ms-minio
ENV SECRET_CREDENTIAL_KEYCLOAK gucci

ENV ZIPKIN_URL http://localhost:9411

ENV POSTGRES_PASSWORD pgpass
ENV POSTGRES_USERNAME pguser
ENV POSTGRES_URL jdbc:postgresql://localhost:5432/skitter

ENV EUREKA_URL http://localhost:8707/eureka/

ENV MINIO_URL http://localhost:9000
ENV MINIO_BUCKET=prod
ENV MINIO_ACCESS_KEY minioak
ENV MINIO_SECRET_KEY miniosk
ENTRYPOINT ["java", "-cp","app:app/lib/*", "com.ucb.bo.sktmsminio.SktMsMinioApplicationKt"]


