#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
PENCIL='\xE2\x9C\x8F'

build_procedure () {
  printf "${PENCIL}  Using ant to build ${RED}Upstream Editor${NC} jar binary... \n"
  ant
  printf "${PENCIL}  ${RED}Upstream Editor${NC} build complete \n"
  rm -r build
  printf "${PENCIL}  Starting editor... \n"
  java -jar dist/UpstreamEditor_*.jar \
             maps/level_1-layer_0.txt \
             maps/level_1-layer_1.txt
}

if command -v lein >/dev/null 2>&1; then
  build_procedure
else
  printf "${PENCIL}  ${Yellow}Warning:${NC} ${RED}ant${NC} not installed. \n"
  printf "${PENCIL}  Attempting ${RED}ant${NC} installation with brew \n"
  brew install ant
  build_procedure
fi
