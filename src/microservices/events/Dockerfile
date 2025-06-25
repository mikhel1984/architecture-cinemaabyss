FROM openjdk:21-jdk-slim

COPY target/events-*-exec.jar /app.jar
RUN chmod 777 /app.jar
ENV JAVA_OPTS --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED
ENTRYPOINT ["/app.jar"]

EXPOSE 8082
