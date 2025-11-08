#Build-stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# for minimal image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/ad-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9555

ENTRYPOINT ["java", "-jar", "app.jar"]