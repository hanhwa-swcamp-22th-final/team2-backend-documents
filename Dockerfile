FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x ./gradlew && ./gradlew clean build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
