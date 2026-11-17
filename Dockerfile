# Two stages: build the .jar with the full JDK, then copy only the .jar into a small runtime image.

# ---- stage 1: build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
# copy the Maven files first so Docker can cache downloaded dependencies between builds
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline
# now the source; tests already run in CI, so skip them here
COPY src src
RUN ./mvnw -q package -DskipTests

# ---- stage 2: run ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
