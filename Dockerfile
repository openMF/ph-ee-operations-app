FROM openjdk:17-bullseye
EXPOSE 5000
WORKDIR /app

COPY build/libs/*.jar .
CMD java -jar *.jar
