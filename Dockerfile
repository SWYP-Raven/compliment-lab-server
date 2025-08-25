
FROM openjdk:17-jdk

# Set the working directory
WORKDIR /app

COPY --from=builder /app/build/libs/compliment-lab-server-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the application port
EXPOSE 8080

        # Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]