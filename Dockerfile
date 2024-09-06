FROM node:16.13.1-alpine

WORKDIR /opt/app

ADD docker_files/OpenJDK21U-jre_x64_alpine-linux_hotspot_21.0.4_7.tar.gz /opt/app/java
#JDK17 min

COPY target/lib-home-*.jar /opt/app/japp.jar

ENV DATA_PATH=/opt/app/data/
ENV DB_FILE_IN_DATA_DIR=lib_home.db

CMD ["/opt/app/java/jdk-21.0.4+7-jre/bin/java", "-jar", "-Ddata.path=${DATA_PATH}", "-Ddb.file=${DB_FILE_IN_DATA_DIR}", "-Dspring.profiles.active=production", "/opt/app/japp.jar"]

EXPOSE 8080