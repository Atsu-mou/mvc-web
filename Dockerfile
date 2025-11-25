# Stage 1: Build the application using a Gradle image
FROM gradle:8.5-jdk17-jammy AS builder
WORKDIR /app

# Copy the entire project to the container
COPY . .

RUN chmod +x ./gradlew

# Build the application, creating a fat JAR using the shadowJar task
RUN ./gradlew shadowJar --info

# Copy the produced jar to a stable filename (real file, not symlink)
RUN set -eux; \
    JAR=$(ls build/libs/*all.jar 2>/dev/null || ls build/libs/*.jar); \
    JAR=$(echo "$JAR" | awk '{print $1}'); \
    echo "Found jar: $JAR"; \
    cp "$JAR" build/libs/app.jar; \
    ls -la build/libs

# Stage 2: Create the final, lightweight runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the stable app.jar produced above
COPY --from=builder /app/build/libs/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
