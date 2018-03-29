FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar

ADD docker/run.sh /run.sh
RUN chmod a+x /run.sh

ADD docker/start_web_interface /start_web_interface
RUN chmod a+x /start_web_interface

ADD docker/redirect_logs /redirect_logs
RUN chmod a+x /redirect_logs
RUN apk update && apk add curl && apk add python

EXPOSE 4000
EXPOSE 4001

CMD /run.sh
