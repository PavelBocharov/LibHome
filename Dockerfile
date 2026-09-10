FROM node:16.13.1-alpine AS builder

RUN apk add --no-cache openjdk17-jdk maven msttcorefonts-installer fontconfig
RUN update-ms-fonts

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH

WORKDIR /opt/app

COPY pom.xml ./pom.xml
COPY LibHomeData/pom.xml ./LibHomeData/pom.xml
COPY LibHomeDB/pom.xml ./LibHomeDB/pom.xml
COPY LibHomePostgresDB/pom.xml ./LibHomePostgresDB/pom.xml
COPY LibHomeUI/pom.xml ./LibHomeUI/pom.xml
COPY LibHomeDBMigrator/pom.xml ./LibHomeDBMigrator/pom.xml

RUN mvn dependency:go-offline -B

COPY LibHomeData/src ./LibHomeData/src
COPY LibHomeDB/src ./LibHomeDB/src
COPY LibHomePostgresDB/src ./LibHomePostgresDB/src
COPY LibHomeUI/src ./LibHomeUI/src
COPY LibHomeDBMigrator/src ./LibHomeDBMigrator/src
COPY LibHomeUI/content.json ./LibHomeUI/content.json

RUN mvn clean install -Pproduction -Doffline=true
