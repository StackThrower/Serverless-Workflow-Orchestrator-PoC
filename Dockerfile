FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the built JAR file
COPY workflow-api/target/workflow-api-*.jar app.jar

# Copy workflow definitions
COPY workflow-api/src/main/resources/workflows /app/workflows

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
