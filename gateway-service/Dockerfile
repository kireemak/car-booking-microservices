FROM maven:3-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml ./eureka-server/
COPY gateway-service/pom.xml ./gateway-service/
COPY user-service/pom.xml ./user-service/
COPY booking-service/pom.xml ./booking-service/
COPY car-details-service/pom.xml ./car-details-service/
COPY car-service/pom.xml ./car-service/
RUN mvn -e -B dependency:go-offline -pl gateway-service
COPY ./gateway-service/src ./gateway-service/src
RUN mvn -e -B -pl gateway-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S gateway && adduser -S gateway -G gateway
USER gateway:gateway
WORKDIR /app
COPY --chown=gateway:gateway --from=build /app/gateway-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
