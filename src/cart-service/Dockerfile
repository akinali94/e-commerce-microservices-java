FROM openjdk:17-slim

WORKDIR /app

COPY target/cart-service-*.jar app.jar

# Environment variables with defaults
# Use SPRING_PROFILES_ACTIVE=redis for Redis
# Use SPRING_PROFILES_ACTIVE=dynamodb for DynamoDB
ENV SPRING_PROFILES_ACTIVE=redis

# Redis configuration
ENV REDIS_ADDR=localhost
ENV REDIS_PORT=6379

# AWS configuration (used for DynamoDB)
ENV AWS_REGION=us-east-1
ENV DYNAMODB_TABLE_NAME=cart_items

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]