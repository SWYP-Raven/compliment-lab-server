FROM --platform=linux/amd64 eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 빌드된 jar 파일 복사
COPY build/libs/compliment-lab-server-0.0.1-SNAPSHOT.jar app.jar

# 포트 설정
EXPOSE 8080

# Spring profile release 지정
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]