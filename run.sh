#!/bin/bash
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

printf "${PLAYER}  Starting ${RED}Upstream${NC} in ${YELLOW}server${NC} mode. \n"
exec java -jar /app.jar -server
