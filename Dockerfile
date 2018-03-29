FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar

ADD docker/run.sh /run.sh
RUN chmod a+x /run.sh

ADD docker/start_web_interface /start_web_interface
RUN chmod a+x /start_web_interface

ADD docker/interface.html /interface.html

ADD docker/redirect_logs /redirect_logs
RUN chmod a+x /redirect_logs

RUN apk update
RUN apk add curl
RUN apk add python

EXPOSE 4444
EXPOSE 4041

CMD /run.sh
