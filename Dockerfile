FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
ADD build/libs/PixelMaze-*.jar /app/pixelmaze.jar

ENTRYPOINT ["java", "-jar", "/app/pixelmaze.jar"]
CMD ["--help"]


