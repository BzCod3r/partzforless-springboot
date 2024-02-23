FROM maven:4.0.0 AS build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean
RUN mvn package  -DskipTests -X

FROM openjdk
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["Java","-jar","app.jar"]