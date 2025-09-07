# 1️⃣ Use lightweight Java runtime
FROM eclipse-temurin:21-jre-alpine

# 2️⃣ Working directory inside container
WORKDIR /app

# 3️⃣ Copy the built JAR into the container
COPY build/libs/*.jar app.jar

# 4️⃣ Expose app port
EXPOSE 8080

# 5️⃣ Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
