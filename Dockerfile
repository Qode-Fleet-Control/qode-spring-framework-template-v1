# Built by .github/workflows/deploy.yml and pushed to Artifact Registry.
#
# Two stages: Maven builds target/app.jar plus its runtime jars in target/lib
# (the jar's manifest names them), a JRE-only image runs it as a non-root user.
# Application.main reads $PORT at RUNTIME, never baked in.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
ARG BUILD_ID=""
WORKDIR /app
ENV PORT=8080 BUILD_ID=$BUILD_ID
RUN useradd -r -u 10001 app
COPY --from=build /src/target/lib /app/lib
COPY --from=build /src/target/app.jar /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
