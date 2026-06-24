# Estágio 1: Build (Compila o código usando o Maven)
FROM maven:3.9.6-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila o projeto ignorando os testes para ser mais rápido na nuvem
RUN mvn clean package -DskipTests

# Estágio 2: Run (Roda a aplicação compilada em um servidor Java leve)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar
# Expõe a porta que o Spring Boot usa por padrão
EXPOSE 8080
# Comando para rodar a API
ENTRYPOINT ["java", "-jar", "app.jar"]