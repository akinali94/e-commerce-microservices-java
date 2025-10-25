# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN addgroup --system --gid 1001 appuser \
    && adduser --system --uid 1001 --gid 1001 appuser


COPY --from=build /app/target/*.jar app.jar

RUN chown -R appuser:appuser /app

USER appuser

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=50051

EXPOSE 50051

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]