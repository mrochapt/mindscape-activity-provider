# Etapa 1: build com Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar o pom e as sources
COPY pom.xml .
COPY src ./src

# Compilar o projeto (sem testes para ser mais rápido)
RUN mvn -q -DskipTests package

# Etapa 2: imagem final só com o JRE e o JAR
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar o JAR gerado na etapa de build
COPY --from=build /app/target/mindscape-activity-provider-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
