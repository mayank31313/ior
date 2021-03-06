FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
EXPOSE 5001
EXPOSE 8000
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]