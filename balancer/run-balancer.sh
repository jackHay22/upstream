#!/bin/sh
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

CONFIG_FILE=$1

printf "${WRENCH}  ${YELLOW}Docker${NC}: Starting ${RED}UpstreamBalancer${NC} service using ${YELLOW}$CONFIG_FILE${NC}. \n"
exec java -jar /app.jar $CONFIG_FILE
