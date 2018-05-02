#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
PENCIL='\xE2\x9C\x8F'

if [ "$#" -ne 2 ]; then
    printf "${WRENCH}  ${RED}Error${NC}: wrong number of args passed to build. \n"
    printf "${WRENCH}  ${YELLOW}Usage${NC}: ./build-balancer <config file> <balancer port> . \n"
fi

start_docker() {
  open -a Docker || exit 1
  i=0
  while ! docker system info &>/dev/null; do
    (( i++ == 0 )) && printf "${WRENCH}  Waiting for ${YELLOW}Docker${NC} daemon" %s || printf "."
    sleep 1
  done
  (( i )) && printf '\n'
  printf "${WRENCH}  ${YELLOW}Docker${NC}: started daemon successfully. \n"
}
build_procedure () {
  printf "${PENCIL}  Using ant to build ${RED}UpstreamBalancer${NC} jar binary... \n"
  printf "${PENCIL}  Found build configuration: ${YELLOW}$(find . -name '*.xml')${NC}. \n"
  ant
  printf "${PENCIL}  ${RED}UpstreamBalancer${NC} ant build complete \n"
  rm -r build
}
docker_procedure () {
  printf "${WRENCH}  ${YELLOW}Docker${NC}: building image. \n"
  docker build --tag upstream_balancer . || start_docker
  printf "${WRENCH}  ${YELLOW}Docker${NC}: starting container. \n"
  docker run --env RUN_CONFIG=$1 --env SERVER_PORT=$2 upstream_balancer:latest
}

if command -v ant >/dev/null 2>&1; then
  build_procedure
  docker_procedure
else
  printf "${PENCIL}  ${Yellow}Warning:${NC} ${RED}ant${NC} not installed. \n"
  printf "${PENCIL}  Attempting ${RED}ant${NC} installation with brew \n"
  brew install ant
  build_procedure
  docker_procedure
fi
