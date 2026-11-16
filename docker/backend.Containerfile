# Build:  podman build -f docker/backend.Containerfile -t armario/backend:latest .
# Run:    podman run --rm -p 8080:8080 armario/backend:latest

FROM docker.io/maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Libreria del proyecto 11
COPY 11-sistema-base-captura-orden/dist /opt/lib

COPY backend/ .
RUN mvn -q -DskipTests -Dlib.11.path=/opt/lib package

FROM docker.io/eclipse-temurin:17-alpine
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]