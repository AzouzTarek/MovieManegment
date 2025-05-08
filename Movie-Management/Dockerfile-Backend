FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

# Copier le pom.xml et télécharger les dépendances
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Rendre le script mvnw exécutable
RUN chmod +x ./mvnw
# Construire le jar
RUN ./mvnw install -DskipTests

# Extraire les couches du jar pour optimiser l'image Docker
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
