FROM maven:3.8.3-jdk-11 AS build
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=1234
ENV DB_NAME=news_portal
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV APP_PORT=8080
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:go-offline
COPY . /app
RUN mvn clean
RUN mvn package

FROM openjdk:17-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]
