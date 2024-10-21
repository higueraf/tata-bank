FROM maven:3.9.7-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/bank-0.0.1.jar tata-bank.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","tata-bank.jar"]