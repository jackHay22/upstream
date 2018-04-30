FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ENV WEB_INTERFACE=4444
ENV SERVER_ARGS=-server
ENV WELCOME_SERVER=4000

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar

ADD docker/run.sh /run.sh
RUN chmod a+x /run.sh

ADD docker/start_web_interface /start_web_interface
RUN chmod a+x /start_web_interface

ADD docker/interface.html /interface.html

ADD docker/redirect_logs /redirect_logs
RUN chmod a+x /redirect_logs

RUN mkdir gp_volume

RUN apk update
RUN apk add curl
RUN apk add python

EXPOSE $WEB_INTERFACE
EXPOSE $WELCOME_SERVER

CMD /run.sh $SERVER_ARGS $WEB_INTERFACE $WELCOME_SERVER
