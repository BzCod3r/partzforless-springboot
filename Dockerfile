# Stage 1: Build the application
FROM maven:3.9.6 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Stage 2: Create the final Docker image
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar ./demo-aws.jar
EXPOSE 8080
CMD ["java", "-jar", "demo-aws.jar"]
