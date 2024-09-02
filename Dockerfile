FROM gradle:8.8.0-jdk21 as BUILDER

# Set the working directory
WORKDIR /app

# Copy the necessary Gradle and project files
COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY . .

# Build the project with caching for dependencies
RUN --mount=type=cache,target=~/.gradle/caches ./gradlew --no-daemon -i clean build

# Use a slim OpenJDK image for the final stage
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=BUILDER /app/build/libs/*.jar app.jar

# Specify the entry point for the application
ENTRYPOINT ["java", "-jar", "app.jar"]
