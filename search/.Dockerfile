FROM openjdk:17
MAINTAINER av.pushking@gmail.com
COPY ./target/search-0.1.jar search.jar
ENTRYPOINT ["java","-jar","/search.jar"]