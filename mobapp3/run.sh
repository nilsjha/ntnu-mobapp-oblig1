#!/bin/sh
CONTAINER=mobapp3
docker container rm -f $CONTAINER
docker run  -p 9080:9080 -p 9443:9443 --name $CONTAINER -v $(pwd)/target:/config/dropins no.nilsjh.ntnu/$CONTAINER