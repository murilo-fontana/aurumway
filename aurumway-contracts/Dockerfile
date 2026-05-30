# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Cache dependencies: copy wrapper + pom first, resolve offline
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline

# Build the application
COPY src src
RUN ./mvnw -B -q clean package -DskipTests

# Extract Spring Boot layered jar for better runtime layer caching
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination extracted \
    && mv extracted/application/*.jar extracted/application/app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Run as non-root
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy layers in cache-friendly order (least to most frequently changing)
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
