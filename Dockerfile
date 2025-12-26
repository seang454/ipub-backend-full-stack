# -----------------------------
# 1️⃣ Build Stage (Optional: Multi-stage build)
# -----------------------------
FROM gradle:8.3.3-jdk21 AS builder

# Set working directory inside container
WORKDIR /app

# Copy Gradle wrapper and build scripts
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY gradle/ ./gradle/

# Copy all source code
COPY src/ ./src/

# Set Gradle user home for caching (optional)
ENV GRADLE_USER_HOME=/tmp/.gradle

# Build fat JAR
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

# Expose port
EXPOSE 8083

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
