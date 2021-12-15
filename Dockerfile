FROM openjdk:8
EXPOSE 8080
ADD target/propicks-docker.jar propicks-docker.jar
ENTRYPOINT ["java", "-jar", "/propicks-docker.jar"]