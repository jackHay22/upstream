FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar

ADD run.sh /run.sh
RUN chmod a+x /run.sh

EXPOSE 4000

CMD /run.sh
