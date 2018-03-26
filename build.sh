#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

AWS_ACCOUNT='190175714341'
ECR_RESOURCE_URI='dkr.ecr.us-east-2.amazonaws.com/upstream_server'

JAVA_RUNTIME=`/usr/libexec/java_home -v 1.8`

lein_build () {
  lein deps
  lein uberjar || exit 1
}

#check lein installation
if command -v lein >/dev/null 2>&1; then
  printf "${WRENCH}  Building ${RED}Upstream${NC} jar binary... ${YELLOW}${1}${NC} \n"
  lein_build
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
    lein_build
  fi
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
elif [ "$1" == "-saveartifact" ]; then
  printf "${WRENCH}  Uploading ${RED}Upstream${NC} jar build to AWS s3 as: ${YELLOW}s3://upstream-build-archive/upstream-archive-build.jar${NC} using s3 versioning scheme. \n"
  aws s3 cp \
  target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar \
  s3://upstream-build-archive/upstream-archive-build.jar
elif [ "$1" == "-linuxserver" ]; then
  printf "${WRENCH}  Building ${RED}Upstream${NC} in ${YELLOW}server mode${NC}... \n"
  docker build --tag upstream_server . || exit 1
  printf "${WRENCH}  Tagging ${RED}upstream_server:latest${NC} as ${YELLOW}190175714341.dkr.ecr.us-east-2.amazonaws.com/upstream_server:latest${NC} \n"
  docker tag upstream_server:latest ${AWS_ACCOUNT}.${ECR_RESOURCE_URI}:latest
  printf "${WRENCH}  Trying ECR login for ${YELLOW}--region us-east-2${NC}. \n"
  eval $(aws ecr get-login --region us-east-2 --no-include-email)
  printf "${WRENCH}  Pushing ${RED}upstream_server:latest${NC} to AWS ECR with URI: ${YELLOW}$ECR_RESOURCE_URI${NC}... \n"
  docker push ${AWS_ACCOUNT}.${ECR_RESOURCE_URI}:latest || exit 1
  printf "${WRENCH}  ECR: ${RED}upstream_server:latest${NC} pushed. \n"
else
  printf "${WRENCH}  Error: ${YELLOW}"$1"${NC} not a valid build mode. \n"
  exit 1
fi
