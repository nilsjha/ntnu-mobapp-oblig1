@echo off
call mvn clean package
call docker build -t no.nilsjh.ntnu/mobapp3 .
call docker rm -f mobapp3
call docker run -d -p 9080:9080 -p 9443:9443 --name mobapp3 no.nilsjh.ntnu/mobapp3