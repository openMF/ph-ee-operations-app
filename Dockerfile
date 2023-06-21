FROM openjdk:17-bullseye
EXPOSE 5000
WORKDIR /app

COPY target/*.jar .
CMD java -jar *.jar
