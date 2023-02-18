FROM amazoncorretto:17
COPY target/CSYE6225-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
EXPOSE 3306



ENTRYPOINT ["java","-jar","/app.jar"]