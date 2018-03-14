#!/bin/sh
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

printf "Starting X11 server for window..."
Xvfb :99 -screen 0 640x480x8 -nolisten tcp &
firefox

printf "Starting ${RED}Upstream${NC} in ${YELLOW}server${NC} mode. \n"
exec java -jar /app.jar -server
