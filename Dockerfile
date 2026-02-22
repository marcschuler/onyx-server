FROM maven:3-eclipse-temurin-25-alpine as builder
COPY . .
RUN mvn package -DskipTests

FROM eclipse-temurin:25
ARG JAR_FILE=target/*.jar
COPY --from=builder ${JAR_FILE} onyx-server.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","./onyx-server.jar"]