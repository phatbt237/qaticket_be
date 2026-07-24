# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies separately from source so code changes don't re-download the world.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline


COPY src ./src
RUN mvn -B -q package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN addgroup --system app && adduser --system --ingroup app app
COPY --from=build /app/target/qms-backend-*.jar app.jar
USER app

# Render sets $PORT and routes traffic to it; application.properties reads it via ${PORT:...}.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
