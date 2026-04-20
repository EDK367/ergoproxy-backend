FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

EXPOSE 8080

COPY target/ergoproxy-backend-0.0.1-SNAPSHOT.jar ergoproxy-backend.jar

ENTRYPOINT ["java", "-jar", "ergoproxy-backend.jar"]


# instrucciones
# docker build -t ergoproxy-backend .
