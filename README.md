# Assignment 1 - ID303911
A server backend written with JavaEE, utilizing PostgreSQL as persistence backend. Due to previous issues the source folder for the Maven project is located in `./mobapp4`. Please, create the required config file before building.

### To build
 1. Go to the project root path in the `mobapp4` -folder: `cd mobapp4`
 1. Create the file `./src/main/resources/META-INF/microprofile-config.properties` 
 1. Insert the parameters from the example file at `./src/main/resources/META-INF/microprofile-config.properties.default`
 1. Import the project into your IDE as a Maven project.
 1. With the default persistance context the database will drop and recreate the tables at each run using JPA.
