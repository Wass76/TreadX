# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Set the working directory
WORKDIR /build

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application (Maven will download dependencies and cache them)
# -Dmaven.test.skip=true skips both test compilation and execution
RUN mvn clean package -Dmaven.test.skip=true -B

# Stage 2: Runtime image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/TreadX-0.0.1-SNAPSHOT.jar /app/treadx.jar

# Expose the port your Spring Boot application listens on (9003)
EXPOSE 9003

# Define the command to run your Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/treadx.jar"]