# Important about docker
Tested with `docker-compose version 1.27.1, build 509cfb99; Docker version 19.03.12, build 48a66213fe`

## Installation
Docker Compose uses environment variables to create the database and bind the services, therefore it's important to create the **.env** file before running any docker commands.

1. Create .env file in this dir to hold environment variables. See .env.example for required variables.
1. Link or copy the whised *docker-compose* depending on your enviroment
   * **When running on server:** Use the *docker-compose-prod.yml* to generate ssl certs and expose the service to a domain. This can be done with `ln -s docker-compose-prod.yml docker-compose.yml` in UNI\*X.
   * **When running on locally with locahost:** Use the *docker-compose-dev.yml* to set up the service with ports mapped to `localhost`. Payara, PostgreSQL will work, Traefik will not be invoked.
3. Set the required enviroment variables for the JavaEE Application (*"the server"*) by creating `src/main/resources/META-INF/microprofile-config.properties`. A template is available named `src/main/resources/META-INF/microprofile-config.properties.default`.

## Operation
* **Build & run detached :** `COMPOSE_DOCKER_CLI_BUILD=1 DOCKER_BUILDKIT=1 docker-compose up -d --build`
* **Run detached :** `docker-compose up -d`
* **Follow detached logs:** `docker-compose logs -f`

### Dropping the database
1. **Stop:** `docker-compose down`
1. **Delete volume:** `docker volume rm mobapp4_database-data`

