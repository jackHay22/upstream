#!/bin/bash

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
WRENCH='\xF0\x9F\x94\xA7'

AWS_ACCOUNT='190175714341'

if [ "$#" -ne 1 ]; then
    printf "${WRENCH}  ${RED}Error:${NC} not enough arguments provided. Usage: ${YELLOW}./launchinstance [ -dev | -production ] <task definition>${NC}. \n"
    exit 1
fi
if [ "$1" == "-dev" ]; then
  aws cloudformation deploy \
    --template-file UpstreamStack.json \
    --stack-name upstreamServer
  printf "${WRENCH}  Launching ${RED}Upstream${NC} task definition. \n"
elif [ "$1" == "-production" ]; then
  printf "${WRENCH}  ${RED}Production${NC} mode under construction. \n"
else
  printf "${WRENCH}  ${RED}Error:${NC} Launch mode ${YELLOW}$1${NC} not understood. \n"
fi
