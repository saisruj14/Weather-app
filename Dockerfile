FROM openjdk:23
ADD target/springboot-mysql-docker.jar springboot-mysql-docker.jar
ENTRYPOINT ["java","-jar","/springboot-mysql-docker.jar"]