FROM openjdk:17-bullseye
EXPOSE 5000
WORKDIR /app

COPY target/*.jar /app/
CMD java -jar *.jar

