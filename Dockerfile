FROM openjdk:17-jdk-slim
WORKDIR /app

# Use the build artifacts from the CI pipeline.
COPY "target/bookstore-1.0.0-SNAPSHOT.jar" "app.jar"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

