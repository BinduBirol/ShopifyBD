FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY target/commerce-auth-service-1.0.0.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]