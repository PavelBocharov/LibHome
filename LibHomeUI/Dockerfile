FROM libhome-build:latest AS base
FROM eclipse-temurin:17-jre-alpine

COPY --from=base /usr/share/fonts/ /usr/share/fonts/

WORKDIR /opt/app

COPY --from=base /opt/app/LibHomeUI/target/LibHomeUI-*.jar /opt/app/japp.jar
COPY --from=base /opt/app/LibHomeUI/content.json /opt/app/data/content.json

ENV LIB_DB_HOST=localhost
ENV LIB_DB_PORT=8088
ENV DATA_PATH=/opt/app/data/
ENV VIEW_CONTENT_JSON=/opt/app/data/content.json

CMD ["java", "-jar", "-Dapp.data.content.file=${VIEW_CONTENT_JSON}", "-Dapp.data.path=${DATA_PATH}", "-Dspring.profiles.active=production", "-Dlibhome.db.url=${LIB_DB_HOST}", "-Dlibhome.db.port=${LIB_DB_PORT}", "/opt/app/japp.jar"]

EXPOSE 8080