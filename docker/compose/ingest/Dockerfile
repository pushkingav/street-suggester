FROM openjdk:17
MAINTAINER av.pushking@gmail.com
COPY ./ingest/*.jar /ingest.jar
COPY ../application.properties /config/application.properties
ENTRYPOINT ["java","-jar","/ingest.jar"]