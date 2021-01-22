<<<<<<< HEAD
FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar

=======
FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/Atalaya.jar
ADD ${JAR_FILE} app.jar

>>>>>>> refs/remotes/origin/main
ENTRYPOINT ["java","-jar","/app.jar"]