# Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Run

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar


EXPOSE 8080


CMD ["/bin/sh", "-c", "\
java -jar app.jar & \
APP_PID=$!; \
echo 'Waiting for Spring Boot to start...'; \
until curl -s http://localhost:8080/api/municipalities > /dev/null 2>&1; do sleep 1; done; \
echo '\n===> Running POST Load Command...'; \
curl -X POST http://localhost:8080/api/municipalities/load \
     -H 'Content-Type: application/json' \
     -d '{\"url\":\"https://www.smartform.cz/download/kopidlno.xml.zip\"}'; \
echo '\n\n===> Running GET Municipalities Command...'; \
curl -s -X GET http://localhost:8080/api/municipalities; \
echo '\n'; \
wait $APP_PID \
"]