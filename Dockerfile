FROM node:16.13.1-alpine

WORKDIR /opt/app

ADD docker_files/OpenJDK8U-jdk_x64_alpine-linux_hotspot_8u372b07.tar.gz /opt/app/java
#JDK8 - https://adoptium.net/temurin/archive/?version=8

COPY target/dark-sun-0.0.1-SNAPSHOT.jar /opt/app/japp.jar
COPY start.sh /opt/app/start.sh

CMD /opt/app/start.sh

EXPOSE 8080