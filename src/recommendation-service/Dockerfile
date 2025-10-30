FROM eclipse-temurin:11-jdk as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=builder /app/target/recommendation-service-*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "app.jar"]