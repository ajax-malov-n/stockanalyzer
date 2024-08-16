LABEL authors="Nikita Malov"

FROM gradle:8.3.0-jdk21 AS build
WORKDIR /app
COPY . .

RUN gradle clean build --no-daemon
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]