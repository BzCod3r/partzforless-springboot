# OpenJDK 17 Alpine version
FROM bellsoft/liberica-openjdk-alpine-musl:17 as build

LABEL authors="muhammadmansoor"

LABEL NAME="my-spring-boot-app"

# Use a base image with Maven and Java pre-installed
WORKDIR /app

# Copy the Maven project file and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN ./mvnw -B clean package -e -X

# Create the final image
FROM bellsoft/liberica-openjdk-alpine-musl:17
WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the JAR file when the container starts
CMD ["java", "-jar", "app.jar"]
