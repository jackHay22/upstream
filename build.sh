#!/bin/bash
#requires lein to build in target
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
PLAYER='\xE2\x9A\xBD'
printf "${PLAYER}  Building ${RED}Upstream${NC} jar binary... ${YELLOW}${1}${NC} \n"
lein uberjar
printf "${PLAYER}  Building ${RED}Upstream${NC} app package... ${YELLOW}${1}${NC} \n"
javapackager -deploy \
    -native image \
    -outdir out \
    -outfile upstream.app \
    -srcfiles target/uberjar/upstream-0.1.0-SNAPSHOT-standalone.jar \
    -appclass upstream.core \
    -name "Upstream" \
    -title "Upstream" \
    -Bruntime= \
    -Bicon=resources/images/Upstream.icns
