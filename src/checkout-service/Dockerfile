#Build-stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

#Runtime-stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/checkout-service-0.0.1-SNAPSHOT.jar app.jar

#COPY src/main/resources/application.properties .

EXPOSE 9557

ENTRYPOINT ["java", "-jar", "app.jar"]