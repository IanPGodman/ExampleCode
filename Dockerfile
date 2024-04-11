FROM openjdk:21-bookworm
LABEL authors="ian"
WORKDIR /app
COPY build/libs/coding.example.jar app.jar
EXPOSE 8080 8443
ENTRYPOINT ["java", "-Dspring.profiles.active=h2", "-jar", "app.jar"]