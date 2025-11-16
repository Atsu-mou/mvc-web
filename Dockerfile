# Stage 1: Build the application using a Gradle image
FROM gradle:8.5-jdk17-jammy AS builder
WORKDIR /app

# Copy the entire project to the container
COPY . .

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the application, creating a fat JAR using the shadowJar task
# We add --info to get more detailed build logs
RUN ./gradlew shadowJar --info

# --- DEBUG: List the contents of the libs directory ---
RUN ls -R /app/build/libs

# Stage 2: Create the final, lightweight runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# --- IMPORTANT: Update the filename below based on the output of the 'ls' command above ---
COPY --from=builder /app/build/libs/mvc-web-0.0.1-all.jar /app/app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
