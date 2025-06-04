FROM openjdk:21-jdk-slim
ENV JAVA_HOME=/usr/local/openjdk-17
WORKDIR /app
COPY target/TreadX-0.0.1-SNAPSHOT.jar /app/treadx.jar
EXPOSE 3003
ENTRYPOINT ["java", "-jar", "/app/treadx.jar", "mvn spring-boot:run"]
