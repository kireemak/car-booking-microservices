FROM maven:3-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml ./eureka-server/
COPY gateway-service/pom.xml ./gateway-service/
COPY user-service/pom.xml ./user-service/
COPY booking-service/pom.xml ./booking-service/
COPY car-details-service/pom.xml ./car-details-service/
COPY car-service/pom.xml ./car-service/
RUN mvn -e -B dependency:go-offline -pl user-service
COPY ./user-service/src ./user-service/src
RUN mvn -e -B -pl user-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S user_service && adduser -S user_service -G user_service
USER user_service:user_service
WORKDIR /app
COPY --chown=user_service:user_service --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]