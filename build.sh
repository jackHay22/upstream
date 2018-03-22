#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'
ECS_RESOURCE_URI='190175714341.dkr.ecr.us-east-2.amazonaws.com/upstream_server'

JAVA_RUNTIME=`/usr/libexec/java_home -v 1.8`

#check lein installation
if command -v lein >/dev/null 2>&1; then
  printf "${WRENCH}  Building ${RED}Upstream${NC} jar binary... ${YELLOW}${1}${NC} \n"
  lein deps
  lein uberjar || exit 1  #don't attempt to package failed jar
else
  printf "${WRENCH}  Warning: ${YELLOW}lein${NC} not installed, attempting to download with homebrew... \n"
  if command -v brew >/dev/null 2>&1; then
    printf "${WRENCH}  Using brew to install ${YELLOW}lein${NC}... \n"
    brew install leiningen || exit 1
  else
    printf "${WRENCH}  Warning: ${YELLOW}brew${NC} not installed, trying to download now... \n"
    /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    printf "${WRENCH}  Using brew to install ${YELLOW}lein${NC}... \n"
    brew install leiningen || exit 1 #note: this is not the preferred way to install lein
    printf "${WRENCH}  Building ${RED}Upstream${NC} jar binary... ${YELLOW}${1}${NC} \n"
    lein deps
    lein uberjar || exit 1
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
elif [ "$1" == "-linuxserver" ]; then
  printf "${WRENCH}  Building ${RED}Upstream${NC} in ${YELLOW}server mode${NC}... \n"
  docker build --tag upstream_server . || exit 1
  printf "${WRENCH}  Tagging ${RED}upstream_server:latest${NC}. \n"
  docker tag upstream_server:latest 190175714341.dkr.ecr.us-east-2.amazonaws.com/upstream_server:latest
  printf "${WRENCH}  Pushing ${YELLOW}upstream_server:latest${NC} to AWS ECR with URI: ${YELLOW}$ECS_RESOURCE_URI${NC}. \n"
  docker push 190175714341.dkr.ecr.us-east-2.amazonaws.com/upstream_server:latest
  printf "${WRENCH}  ${RED}upstream_server:latest${NC} pushed. \n"
  # docker run --rm \
  #            -e DISPLAY=unix$DISPLAY \
  #            -v /tmp/.X11-unix:/tmp/.X11-unix \
  #            upstream_server:latest
else
  printf "${WRENCH}  Error: ${YELLOW}"$1"${NC} not a valid build mode. \n"
  exit 1
fi
