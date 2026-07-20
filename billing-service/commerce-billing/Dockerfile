FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY target/billing-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]