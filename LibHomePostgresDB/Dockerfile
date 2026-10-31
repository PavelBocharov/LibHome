FROM libhome-build:latest AS base
FROM eclipse-temurin:17-jre-alpine

WORKDIR /opt/app

COPY --from=base /opt/app/LibHomePostgresDB/target/LibHomePostgresDB-*.jar /opt/app/japp.jar

ENV POSTGRES_HOST=localhost
ENV POSTGRES_PORT=8080
ENV POSTGRES_DB=db
ENV POSTGRES_USER=usr
ENV POSTGRES_PASSWORD=pwd

CMD java -jar -Dspring.datasource.url="jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}" "-Dspring.datasource.username=${POSTGRES_USER}" "-Dspring.datasource.password=${POSTGRES_PASSWORD}" /opt/app/japp.jar

EXPOSE 8088