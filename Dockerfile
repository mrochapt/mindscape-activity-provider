# Etapa 1: build do JAR com Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar ficheiros do Maven
COPY pom.xml .
COPY src ./src

# Fazer o build (gera o JAR em target/)
RUN mvn -q -DskipTests package

# Etapa 2: imagem de runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar o JAR da etapa de build
COPY --from=build /app/target/mindscape-activity-provider-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
