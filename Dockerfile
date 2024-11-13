FROM node:16.13.1-alpine

WORKDIR /opt/app

ADD docker_files/OpenJDK*.tar.gz /opt/app/java
#JDK17 min

COPY target/lib-home-*.jar /opt/app/japp.jar
COPY content.json /opt/app/data/content.json

ENV DATA_PATH=/opt/app/data/
ENV DB_FILE_IN_DATA_DIR=lib_home.db
ENV VIEW_CONTENT_JSON=/opt/app/data/content.json

# Look to JAVA dir name '21.0.5_11' -> '21.0.5+11'.
# You can look in archive real dir name.
CMD ["/opt/app/java/jdk-21.0.4+7-jre/bin/java", "-jar", "-Dapp.data.content.file=${VIEW_CONTENT_JSON}", "-Dapp.data.path=${DATA_PATH}", "-Dapp.db.file=${DB_FILE_IN_DATA_DIR}", "-Dspring.profiles.active=production", "/opt/app/japp.jar"]

EXPOSE 8080