FROM node:16.13.1-alpine

WORKDIR /opt/app

ADD docker_files/OpenJDK21U-jre_x64_alpine-linux_hotspot_21.0.4_7.tar.gz /opt/app/java
#JDK17 min

COPY target/lib-home-*.jar /opt/app/japp.jar
COPY content.json /opt/app/data/content.json

ENV DATA_PATH=/opt/app/data/
ENV DB_FILE_IN_DATA_DIR=lib_home.db
ENV VIEW_CONTENT_JSON=/opt/app/data/content.json

CMD ["/opt/app/java/jdk-21.0.4+7-jre/bin/java", "-jar", "-Dapp.data.content.file=${DATA_PATH}", "-Dapp.data.path=${DATA_PATH}", "-Dapp.db.file=${DB_FILE_IN_DATA_DIR}", "-Dspring.profiles.active=production", "/opt/app/japp.jar"]

EXPOSE 8080