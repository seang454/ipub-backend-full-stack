# -----------------------------
# 1️⃣ Builder Stage
# -----------------------------
FROM gradle:8.4.0-jdk21 AS builder

# Set working directory inside container
WORKDIR /app

# Copy Gradle wrapper and build scripts first to leverage caching
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY gradle/ ./gradle/

# Copy source code
COPY src/ ./src/

# Use temporary Gradle cache directory to avoid permission issues
ENV GRADLE_USER_HOME=/tmp/.gradle
# Prevent Gradle daemon issues & limit memory
ENV GRADLE_OPTS="-Xmx2g -Dorg.gradle.daemon=false"

# Make gradlew executable and build fat JAR without tests
RUN chmod +x gradlew \
    && ./gradlew clean build -x test --max-workers=1 --stacktrace

# -----------------------------
# 2️⃣ Runtime Stage
# -----------------------------
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port your Spring Boot app uses
EXPOSE 8084

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
