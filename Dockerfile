FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/mindscape-activity-provider-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
