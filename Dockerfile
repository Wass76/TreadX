# Use Eclipse Temurin JDK 21 image (replacement for deprecated openjdk images)
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built Spring Boot JAR from your target/ directory to /app/treadx.jar in the container
# Ensure that 'target/TreadX-0.0.1-SNAPSHOT.jar' is the correct path and filename after your build.
COPY target/TreadX-0.0.1-SNAPSHOT.jar /app/treadx.jar

# Expose the port your Spring Boot application listens on (9003)
EXPOSE 9003

# Define the command to run your Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/treadx.jar"]