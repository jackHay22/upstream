FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar

ADD docker/run.sh /run.sh
RUN chmod a+x /run.sh

ADD docker/redirect_logs /redirect_logs
RUN chmod a+x /redirect_logs
RUN apk update && apk add curl

EXPOSE 4000

CMD /run.sh
