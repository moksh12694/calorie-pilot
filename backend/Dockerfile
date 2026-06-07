# syntax=docker/dockerfile:1.7

# --- Build stage: cache deps, then build the jar ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies first
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp clean package -DskipTests

# --- Extract Spring Boot layers for better caching ---
FROM eclipse-temurin:17-jre AS extractor
WORKDIR /extract
COPY --from=build /workspace/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# --- Runtime stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# Non-root user
RUN groupadd --system app && useradd --system --gid app --home /app app
USER app

# Layered jar — dependencies first (rarely change) then application classes
COPY --from=extractor /extract/dependencies/         ./
COPY --from=extractor /extract/spring-boot-loader/   ./
COPY --from=extractor /extract/snapshot-dependencies/ ./
COPY --from=extractor /extract/application/          ./

EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
