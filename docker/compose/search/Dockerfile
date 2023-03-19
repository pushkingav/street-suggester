FROM openjdk:17
MAINTAINER av.pushking@gmail.com
COPY ./search/search-0.1.jar search.jar
COPY ../application.properties /config/application.properties
ENTRYPOINT ["java","-jar","/search.jar"]