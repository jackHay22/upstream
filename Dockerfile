FROM openjdk:alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar
ADD run.sh /run.sh
RUN chmod a+x /run.sh

ENV DISPLAY :99

EXPOSE 5555

CMD /run.sh
