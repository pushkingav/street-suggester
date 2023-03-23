FROM openjdk:17
MAINTAINER av.pushking@gmail.com

RUN mkdir /app && mkdir /app/config && mkdir /app/csv

COPY ./ingest/*.jar /app/ingest.jar
COPY ../application.properties /app/config/application.properties
WORKDIR /app

ENTRYPOINT ["java","-Dspring.config.location=/app/config/application.properties","-jar","/app/ingest.jar"]