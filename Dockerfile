from openjdk:latest

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9691

ENTRYPOINT [ "java" , "-jar" , "app.jar" ]