# Estágio 1: Build (Usando JDK 25 e o Maven Wrapper do seu projeto)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copia os arquivos do Maven Wrapper e as configurações
COPY mvnw pom.xml ./
COPY .mvn ./.mvn
COPY src ./src

# Garante que o script do Maven tem permissão para rodar no Linux do Render
RUN chmod +x mvnw

# Compila o projeto ignorando os testes
RUN ./mvnw clean package -DskipTests

# Estágio 2: Run (Usando uma imagem Alpine super leve do Java 25)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]