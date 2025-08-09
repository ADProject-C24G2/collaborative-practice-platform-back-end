FROM eclipse-temurin:11-jre-focal

WORKDIR /app

COPY platform-server/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]