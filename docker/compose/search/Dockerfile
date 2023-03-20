FROM openjdk:17
MAINTAINER av.pushking@gmail.com

RUN mkdir /app && mkdir /app/config

COPY ./search/search-0.1.jar /app/search.jar
COPY ../application.properties /app/config/application.properties
WORKDIR /app

EXPOSE 8080
ENTRYPOINT ["java","-Dspring.config.location=/app/config/application.properties","-jar","/app/search.jar"]