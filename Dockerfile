FROM gradle:jdk11-slim AS BUILD_IMAGE
COPY . /home/source/java
WORKDIR /home/source/java
USER root
RUN chown -R gradle /home/source/java
USER gradle
RUN gradle clean build

FROM openjdk:11-jre
VOLUME /application
WORKDIR /application
COPY --from=BUILD_IMAGE "/home/source/java/build/libs/backend-challenge.jar" app.jar
CMD ["java","-Dspring.data.mongodb.uri=mongodb://springboot-mongo:27017/springmongo-demo","-jar","app.jar"]