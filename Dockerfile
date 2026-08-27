#
#FROM openjdk:17-jdk
#
## Set the working directory
#WORKDIR /app
#
#COPY /build/libs/compliment-lab-server-0.0.1-SNAPSHOT.jar /app/app.jar
#
## Expose the application port
#EXPOSE 8080
#
#        # Set the entry point to run the application
#ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
##ENTRYPOINT ["java", "-jar", "app.jar"]


FROM openjdk:17-jdk

WORKDIR /app

# 빌드된 jar 파일 복사
COPY build/libs/compliment-lab-server-0.0.1-SNAPSHOT.jar app.jar

# 포트 설정
EXPOSE 8080

# Spring profile release 지정
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=release"]