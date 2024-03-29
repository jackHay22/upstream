#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

AWS_ACCOUNT='190175714341'
ECR_RESOURCE_URI='dkr.ecr.us-east-2.amazonaws.com/upstream_server'

JAVA_RUNTIME=`/usr/libexec/java_home -v 1.8`
DATE=`date +%Y-%m-%d`

lein_build () {
  lein deps
  lein uberjar || exit 1
}

build_native () {
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
      -Bicon=resources/app/peavey.icns && \
  printf "${WRENCH}  ${RED}Upstream.app${NC} built to ${YELLOW}/out/bundles/Upstream${NC}. \n"
}

start_docker() {
  printf "${WRENCH}  ${YELLOW}Warning${NC}: trying to start docker daemon. \n"
  open -a Docker || exit 1
  i=0
  while ! docker system info &>/dev/null; do
    (( i++ == 0 )) && printf "${WRENCH}  Waiting for ${YELLOW}Docker${NC} daemon" %s || printf "."
    sleep 1
  done
  (( i )) && printf '\n'
  printf "${WRENCH}  ${YELLOW}Docker${NC}: started daemon successfully. \n"
  printf "${WRENCH}  ${YELLOW}Docker${NC}: retrying build... \n"
  docker build --tag upstream_server .
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
    printf "${WRENCH}  ${YELLOW}Brew${NC}: installing ${RED}lein${NC}... \n"
    brew install leiningen || exit 1 #note: this is not the preferred way to install lein
    printf "${WRENCH}  Building ${RED}Upstream${NC} jar binary... ${YELLOW}${1}${NC} \n"
    lein_build
  fi
fi

if [ $# -eq 0 ]; then
  build_native
elif [ "$1" == "-release" ]; then
  build_native
  BUILD_NAME="Upstream-$(echo target/uberjar/upstream*.jar | awk -F"-" '{ print $2 }').zip"
  VERSION="$(echo target/uberjar/upstream*.jar | awk -F"-" '{ print $2 }')"
  pushd out/bundles
  zip -r $BUILD_NAME ./Upstream.app > /dev/null 2>&1
  printf "${WRENCH}  Pushing build to ${YELLOW}s3${NC} as ${BUILD_NAME}\n"
  aws s3 cp $BUILD_NAME s3://upstream-release/$BUILD_NAME
  rm $BUILD_NAME
  popd
  printf "${WRENCH}  Build ${YELLOW}$VERSION${NC} release complete \n"
elif [ "$1" == "-balancer" ]; then
  printf "${WRENCH}  Note: this tool has been removed and integrated into the main system in server mode."
elif [ "$1" == "-backup" ]; then
  printf "${WRENCH}  Note: omitting ${YELLOW}/target${NC}, ${YELLOW}/out${NC}, "
  printf  "${YELLOW}/editor/dist${NC} and ${YELLOW}/editor/resources${NC} from archive. \n"
  zip -r "upstream_sepulchre_$DATE.zip" . \
          -x /target/**\* /out/**\* \
          /editor/dist**\* /editor/resources**\* > /dev/null 2>&1
  printf "${WRENCH}  Uploading ${RED}Upstream${NC} repo to AWS Glacier: ${YELLOW}upstream_sepulchre${NC}. \n"
  if aws glacier upload-archive \
          --region us-east-2 \
          --vault-name upstream_sepulchre \
          --account-id - \
          --body upstream_sepulchre_*.zip; then
    rm upstream_sepulchre_*.zip
    printf "${WRENCH}  ${YELLOW}Glacier${NC}: repo uploaded with filename: ${YELLOW}upstream_sepulchre_$DATE.zip${NC} \n"
  else
    rm upstream_sepulchre_*.zip
    printf "${WRENCH}  ${YELLOW}Glacier${NC}: ERROR: archive not uploaded to AWS glacier. \n"
    exit 1
  fi
elif [ "$1" == "-server" ]; then
  printf "${WRENCH}  ${YELLOW}Docker${NC}: building ${RED}upstream_server${NC}... \n"
  docker build --tag upstream_server . || start_docker
  if [ "$#" -eq 2 ]; then
    if [ "$2" == "-run" ]; then
      printf "${WRENCH}  ${YELLOW}Docker${NC}: running ${RED}upstream_server${NC} with the following params: \n"
      cat docker/run.list
      docker run --env-file ./docker/run.list upstream_server:latest
    elif [ "$2" == "-push" ]; then
      printf "${WRENCH}  Tagging ${RED}upstream_server:latest${NC} as ${YELLOW}190175714341.dkr.ecr.us-east-2.amazonaws.com/upstream_server:latest${NC} \n"
      docker tag upstream_server:latest ${AWS_ACCOUNT}.${ECR_RESOURCE_URI}:latest
      printf "${WRENCH}  Trying ECR login for ${YELLOW}--region us-east-2${NC}. \n"
      eval $(aws ecr get-login --region us-east-2 --no-include-email)
      printf "${WRENCH}  Pushing ${RED}upstream_server:latest${NC} to AWS ECR with URI: ${YELLOW}$ECR_RESOURCE_URI${NC}... \n"
      docker push ${AWS_ACCOUNT}.${ECR_RESOURCE_URI}:latest || exit 1
      printf "${WRENCH}  ${YELLOW}ECR${NC}: ${RED}upstream_server:latest${NC} pushed. \n"
      printf "${WRENCH}  ${YELLOW}ECR${NC}: Images in upstream_server repository: \n"
      aws ecr describe-images --repository-name upstream_server --region us-east-2
    else
      printf "${WRENCH}  Error: ${YELLOW}"$2"${NC} not a valid server build mode. Use ${YELLOW}-run${NC} or ${YELLOW}-push${NC}\n"
    fi
  fi
else
  printf "${WRENCH}  Error: ${YELLOW}"$1"${NC} not a valid build mode. "
  printf "Use ${YELLOW}-release${NC}, ${YELLOW}-editor${NC}, ${YELLOW}-backup${NC} or ${YELLOW}-server${NC}. \n"
  exit 1
fi
