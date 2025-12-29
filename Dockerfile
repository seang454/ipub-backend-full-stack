# -----------------------------
# 1️⃣ Build Stage
# -----------------------------
FROM gradle:8.3-jdk21 AS builder

# Set working directory inside container
WORKDIR /app

# Copy Gradle wrapper and build scripts
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY gradle/ ./gradle/

# Copy source code
COPY src/ ./src/

# Set Gradle cache
ENV GRADLE_USER_HOME=/tmp/.gradle

# Make gradlew executable and build fat JAR without tests
RUN chmod +x gradlew \
    && ./gradlew clean build -x test --no-daemon --parallel

# -----------------------------
# 2️⃣ Runtime Stage
# -----------------------------
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port that your pipeline maps (8084)
EXPOSE 8084

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
