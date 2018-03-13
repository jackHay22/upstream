FROM openjdk:alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

COPY target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar
COPY run.sh /run.sh

EXPOSE 5555

ENTRYPOINT ["./run.sh"]
