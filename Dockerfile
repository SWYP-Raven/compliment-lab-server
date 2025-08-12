# 1. 빌드 스테이지 (Build Stage)
FROM gradle:8.4.0-jdk17-alpine AS builder
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 2. 실행 스테이지 (Runtime Stage)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/compliment-lab-server-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
