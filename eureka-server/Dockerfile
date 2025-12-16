FROM maven:3-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml ./eureka-server/
COPY gateway-service/pom.xml ./gateway-service/
COPY user-service/pom.xml ./user-service/
COPY booking-service/pom.xml ./booking-service/
COPY car-details-service/pom.xml ./car-details-service/
COPY car-service/pom.xml ./car-service/
RUN mvn -e -B dependency:go-offline -pl eureka-server
COPY ./eureka-server/src ./eureka-server/src
RUN mvn -e -B -pl eureka-server -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S eureka && adduser -S eureka -G eureka
USER eureka:eureka
WORKDIR /app
COPY --chown=eureka:eureka --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]