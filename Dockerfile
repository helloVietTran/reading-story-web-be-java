FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests


FROM amazoncorretto:17.0.15-al2023-headless

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
