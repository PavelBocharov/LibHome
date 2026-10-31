FROM libhome-build:latest AS base
FROM eclipse-temurin:17-jre-alpine

WORKDIR /opt/app

COPY --from=base /opt/app/LibHomeDB/target/LibHomeDB-*.jar /opt/app/japp.jar

ENV MONGO_HOST=localhost
ENV MONGO_PORT=8080
ENV MONGO_DB=db
ENV MONGO_USER=usr
ENV MONGO_PWD=pwd

#CMD ["java", "-jar", "-Dspring.data.mongodb.host=${MONGO_HOST}", "-Dspring.data.mongodb.port=${MONGO_PORT}", "-Dspring.data.mongodb.database=${MONGO_DB}", "-Dspring.data.mongodb.username=${MONGO_USER}", "-Dspring.data.mongodb.password=${MONGO_PWD}", "/opt/app/japp.jar"]
CMD java -jar -Dspring.data.mongodb.uri="mongodb://${MONGO_USER}:${MONGO_PWD}@${MONGO_HOST}:${MONGO_PORT}/${MONGO_DB}?authSource=${MONGO_DB}" /opt/app/japp.jar

EXPOSE 8088