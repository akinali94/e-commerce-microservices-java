FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR file
COPY target/*.jar app.jar

# Copy products.json to data directory
COPY src/main/resources/data /app/data

# Expose the application port
EXPOSE 9555

# Run the application
CMD ["java", "-jar", "/app/app.jar"]