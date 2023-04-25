FROM maven:3.8.3-jdk-11 AS build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:go-offline
COPY . /app
RUN mvn clean
RUN mvn package -DskipTests

FROM openjdk:17-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]
