FROM hypriot/rpi-java

MAINTAINER Jack Hay

ADD target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar app.jar
ADD run.sh /run.sh

EXPOSE 5555

ENTRYPOINT ./run.sh
