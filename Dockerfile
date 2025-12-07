# Fase 1: build com Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copiar o código fonte
COPY src ./src

# Compilar e empacotar
RUN mvn -q -e -DskipTests package

# Fase 2: imagem final só com o JRE e o .jar
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar o .jar gerado na fase de build
COPY --from=build /app/target/mindscape-activity-provider-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
