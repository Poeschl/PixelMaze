FROM openjdk:15-jdk-slim

WORKDIR /app
ADD build/libs/pixelflut-pixelmaze-*.jar /app/pixelmaze.jar

ENTRYPOINT ["java", "-jar", "/app/pixelmaze.jar"]
CMD ["--help"]


