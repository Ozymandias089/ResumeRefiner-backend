# ---- build stage ----
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# 캐시 최적화: 먼저 설정/래퍼만
COPY gradlew settings.gradle build.gradle /app/
COPY gradle /app/gradle
RUN chmod +x /app/gradlew

# 의존성 캐시
RUN ./gradlew --no-daemon dependencies

# 소스 복사 후 bootJar
COPY src /app/src
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Render는 내부 포트를 감지하지만, PORT를 주기도 함(아래 application-prod에 대응 권장)
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
