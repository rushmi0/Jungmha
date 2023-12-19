FROM amazoncorretto:17

WORKDIR /app

COPY . .

RUN chmod +x gradlew; ./gradlew run

EXPOSE 8080:8080
