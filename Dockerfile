FROM eclipse-temurin:17-jre
EXPOSE 5000

COPY build/libs/*.jar .
CMD java -jar *.jar

