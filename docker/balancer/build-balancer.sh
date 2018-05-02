#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
PENCIL='\xE2\x9C\x8F'

build_procedure () {
  printf "${PENCIL}  Using ant to build ${RED}UpstreamBalancer${NC} jar binary... \n"
  printf "${PENCIL}  Found build configuration: ${YELLOW}$(find . -name '*.xml')${NC}. \n"
  ant
  printf "${PENCIL}  ${RED}UpstreamBalancer${NC} ant build complete \n"
  rm -r build
}

if command -v ant >/dev/null 2>&1; then
  build_procedure
else
  printf "${PENCIL}  ${Yellow}Warning:${NC} ${RED}ant${NC} not installed. \n"
  printf "${PENCIL}  Attempting ${RED}ant${NC} installation with brew \n"
  brew install ant
  build_procedure
fi
