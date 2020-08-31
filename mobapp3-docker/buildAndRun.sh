#!/bin/sh
if [ $(docker ps -a -f name=mobapp3 | grep -w mobapp3 | wc -l) -eq 1 ]; then
  docker rm -f mobapp3
fi
mvn clean package && docker build -t no.nilsjh.ntnu/mobapp3 .
docker run -d -p 9080:9080 -p 9443:9443 --name mobapp3 no.nilsjh.ntnu/mobapp3
