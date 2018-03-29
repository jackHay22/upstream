#!/bin/sh
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

#printf "${WRENCH}  ${YELLOW}Docker${NC}: Starting ${RED}Upstream${NC} metrics web interface on ${YELLOW}localhost:4001${NC}\n"
#./start_web_interface & #(something wrong with trying to connect)

printf "${WRENCH}  ${YELLOW}Docker${NC}: Starting ${RED}Upstream${NC} in ${YELLOW}-server${NC} mode. \n"
exec java -jar /app.jar -server | ./redirect_logs
