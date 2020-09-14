# syntax=docker/dockerfile:experimental
FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -f pom.xml clean package
#RUN mvn clean package


FROM payara/server-full AS prod
COPY --from=build /app/target/mobapp4-1.0.war $DEPLOY_DIR/mobapp4.war
#COPY wait-for-postgres.sh /opt/payara/scripts/init_wait-for-postgres.sh
#RUN apt-get update && \
 # apt-get install --no-install-recommends --no-install-suggests postgresql-client-12
