# ============================================================================
# Beijing Metro (北京地铁) Spring Boot Backend Dockerfile
# Multi-stage build to minimize runtime container sizes (JRE-only execution)
# ============================================================================

# Stage 1: Build the package using Gradle/Maven JDK
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Prepare the lightweight container with JRE only
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/beijingmetro-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose standard Spring Boot service port
EXPOSE 8080

# Configure JVM heap parameters for enterprise cloud stability
ENV JVM_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
