FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY target/GoToWork-0.0.1-SNAPSHOT.jar /app/GoToWork.jar
ENTRYPOINT ["java","-jar", "GoToWork.jar"]