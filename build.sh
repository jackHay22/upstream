#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

JAVA_RUNTIME=`/usr/libexec/java_home -v 1.8`

#check lein installation
if command -v lein >/dev/null 2>&1; then
  printf "${WRENCH}  Building ${RED}Upstream${NC} jar binary... ${YELLOW}${1}${NC} \n"
  lein uberjar || exit 1  #don't attempt to package failed jar
else
  printf "${WRENCH}  Warning: ${YELLOW}lein${NC} not installed, attempting to download with homebrew... \n"
  if command -v brew >/dev/null 2>&1; then
    brew install leiningen || exit 1
  else
    printf "${WRENCH}  Error: ${YELLOW}brew${NC} not installed, could not install leingingen... \n"
  fi
  exit 1
fi
if [ $# -eq 0 ]; then
  printf "${WRENCH}  Building ${RED}Upstream${NC} app package... ${YELLOW}${1}${NC} \n"
  javapackager -deploy \
      -native image \
      -outdir out \
      -outfile upstream.app \
      -srcfiles target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar \
      -appclass upstream.core \
      -name "Upstream" \
      -title "Upstream" \
      -Bruntime=${JAVA_RUNTIME} \
      -Bicon=resources/app/Upstream.icns && \
  printf "${WRENCH}  ${RED}Upstream.app${NC} built to ${YELLOW}/out/bundles/Upstream${NC}. \n"
elif [ "$1" == "-server" ]; then
  printf "${WRENCH}  Building ${RED}Upstream${NC} in ${YELLOW}server mode${NC}... \n"
  docker build --tag upstream_server . || exit 1 #GUI will fail
  #docker run -t upstream_server:latest #problem with X11 server (xvfb)
  #docker run -p 5900 -t upstream_server:latest x11vnc -forever -usepw -create
  printf "${WRENCH}  ${RED}upstream_server:latest${NC} created. \n"
else
  printf "${WRENCH}  Error: ${YELLOW}"$1"${NC} not a valid build mode. \n"
  exit 1
fi
