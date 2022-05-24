FROM openjdk:9
EXPOSE 8080
ADD target/propicks-docker.jar propicks-docker.jar
ENTRYPOINT ["java", "-jar", "/propicks-docker.jar"]