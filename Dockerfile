FROM node:16.13.1-alpine AS builder

RUN apk add --no-cache openjdk17-jdk maven

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH

COPY .libs /root/.m2/repository

WORKDIR /opt/app
# Main POM
COPY pom.xml ./pom.xml
# data lib
COPY LibHomeData/src ./LibHomeData/src
COPY LibHomeData/pom.xml ./LibHomeData/pom.xml
# Database
COPY LibHomeDB/src ./LibHomeDB/src
COPY LibHomeDB/pom.xml ./LibHomeDB/pom.xml
# UI
COPY LibHomeUI/src ./LibHomeUI/src
COPY LibHomeUI/pom.xml ./LibHomeUI/pom.xml
COPY LibHomeUI/content.json ./LibHomeUI/content.json
COPY ./.npm_libs/node_modules ./LibHomeUI/node_modules
COPY ./.npm_libs/package.json ./LibHomeUI/package.json
COPY ./.npm_libs/package-lock.json ./LibHomeUI/package-lock.json

RUN mvn clean install -Pproduction