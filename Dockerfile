FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ENV SERVER_ARGS=-server
ENV SERVER_PORT=4000

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar

ADD docker/run.sh /run.sh
RUN chmod a+x /run.sh

ADD docker/redirect_logs /redirect_logs
RUN chmod a+x /redirect_logs

RUN apk update
RUN apk add curl
RUN apk add python

EXPOSE $SERVER_PORT

CMD /run.sh $SERVER_ARGS $SERVER_PORT
