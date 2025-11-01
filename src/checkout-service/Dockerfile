FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy maven executable and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the maven wrapper executable
RUN chmod +x ./mvnw

# Download all dependencies
# Dependencies will be cached if the pom.xml file doesn't change
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Second stage: Setup the runtime container
FROM eclipse-temurin:17-jre

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

VOLUME /tmp

# Copy the built artifacts from the build stage
ARG DEPENDENCY=/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set the startup command
ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.checkoutservice.CheckoutServiceApplication"]

# Expose the port the app runs on
EXPOSE 8080

# Container health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/api/checkout/health || exit 1