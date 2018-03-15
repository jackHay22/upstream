FROM java:8-jre-alpine

MAINTAINER Jack Hay "https://github.com/jackHay22"

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar
RUN apk add javapackager
RUN javapackager -deploy \
    -native image \
    -outdir out \
    -outfile upstream.app \
    -srcfiles app.jar \
    -appclass upstream.core \
    -name "Upstream" \
    -title "Upstream" \
    -Bruntime=${JAVA_RUNTIME} \
ADD run.sh /run.sh
RUN chmod a+x /run.sh

EXPOSE 5555

CMD /run.sh
