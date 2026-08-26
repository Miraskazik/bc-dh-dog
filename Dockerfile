# syntax=docker/dockerfile:1
# Multi-stage build. Build fáze potřebuje stáhnout server stub bc-dh-dog-api
# z GitHub Packages → předej Maven settings.xml jako BuildKit secret:
#   docker build --secret id=m2_settings,src=$HOME/.m2/settings.xml -t bc-dh-dog .
# (settings.xml musí mít server id "github" s tokenem se scope read:packages)

FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=secret,id=m2_settings,target=/root/.m2/settings.xml \
    --mount=type=cache,target=/root/.m2/repository \
    mvn -B -q dependency:go-offline
COPY src ./src
RUN --mount=type=secret,id=m2_settings,target=/root/.m2/settings.xml \
    --mount=type=cache,target=/root/.m2/repository \
    mvn -B -q -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 1001 spring
USER spring
COPY --from=build /app/target/bc-dh-dog-0.1.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
