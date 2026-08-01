# --- Build stage -----------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src/ src/
RUN ./mvnw -q clean package -DskipTests

# --- Runtime stage -----------------------------------------------------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# wget est nécessaire pour le HEALTHCHECK (l'image JRE de base ne l'inclut pas).
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/backend-0.1.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
