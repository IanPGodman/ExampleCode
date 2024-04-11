## Project Setup ##
This document provides steps for setting up the project including SSL certificate generation, keystore creation, building the jar, creating Docker images, and some details about the project configuration.

*** Important Notes:***
For compilation I use a local install of Gradle 8.6. Had issues with the Gradle Wrapper failing on plugin id(“org.springframework.boot”) version “3.2.4”

Additionally you will need you own SpoonacularApiKey which you can obtain free of charge from [spoonacular](https://spoonacular.com/) and add it to the main and test application.yml files were indicated by YOUR_KEY_HERE
## SSL Certificate ##
We use mkcert to generate a certificate for SSL. The certificate is generated for the domain name localhost.local and the keystore can be found in the project root as localhost.local.p12.
Note: The mkcert installs a local certificate authority which is totally local to your machine.

For creating your own local authority, use this command:

**mkcert install**

mkcert available at [mkcert](https://github.com/FiloSottile/mkcert/)

To create the keystore from the generated certificate we used:

** openssl pkcs12 -export -out keystore.p12 -inkey localhost.local-key.pem -in localhost.local.pem -name ‘mycert’ **

## Build and Docker ##
Gradle build task bootJar is used to build the jar which will create an executable jar file in build/libs/coding.example.jar. You can also use the Dockerfile provided to run a container using this jar or create a local docker image using the following command: 

**docker build -t demo_code_image .** (dont forgrt the following ' .')

Several additional tasks have been added to build.gradle.kts to generate a docker image from the jar, which will be used to initialize Testcontainers for testing.

## Database and Testing ##
We use a docker container for Postgres but it can also be run on your host machine. If run on a remote machine the database URL will need to be updated. Most tests run using an in-memory H2 database. Spring profiles are used for configuring either JDBC or JPA (Hibernate ORM) database access. To run in JDBC ‘mode’ in IntelliJ, refer to the configuration snapshot below:

![Run Configuration.png](Run%20Configuration.png)