FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ENV RUN_CONFIG=$RUN_CONFIG
ENV SERVER_PORT=$SERVER_PORT

ADD dist/UpstreamBalancer_*.jar app.jar

ADD docker/run-balancer.sh /run-balancer.sh
RUN chmod a+x /run-balancer.sh

ADD $RUN_CONFIG /$RUN_CONFIG

EXPOSE $SERVER_PORT

CMD /run-balancer.sh $RUN_CONFIG
