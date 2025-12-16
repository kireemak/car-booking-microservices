FROM maven:3-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml ./eureka-server/
COPY gateway-service/pom.xml ./gateway-service/
COPY user-service/pom.xml ./user-service/
COPY booking-service/pom.xml ./booking-service/
COPY car-details-service/pom.xml ./car-details-service/
COPY car-service/pom.xml ./car-service/
RUN mvn -e -B dependency:go-offline -pl car-details-service
COPY ./car-details-service/src ./car-details-service/src
RUN mvn -e -B -pl car-details-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S sys_group && adduser -S sys_user -G sys_group
USER sys_user:sys_group
WORKDIR /app
COPY --chown=sys_user:sys_group --from=build /app/car-details-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]