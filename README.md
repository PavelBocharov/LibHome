# <img src="C:\Users\Marolok\IdeaProjects\vaadinTest\src\main\resources\static\img\icon\database.svg" width="30"/> Dark sun

<img alt="Dar Sun screen" src="src/main/resources/static/img/readmeFiles/dar_sun_screen.png" height="512"/>

## Build and run
### <img src="C:\Users\Marolok\IdeaProjects\vaadinTest\src\main\resources\static\img\icon\arrow-right.png" width="16"/> Start application local 
1) Install JDK8 - https://adoptopenjdk.net/
2) Install NodeJS - https://nodejs.org/en/download/
   * Install nvm - [Windows](https://github.com/coreybutler/nvm-windows), [Linux](https://github.com/nvm-sh/nvm)
3) In project dir run command `npm install`
4) In `Dark Sun Spring Run` edit `Environment variables` - set `dbPath` (path to SQLite db file).
5) Start `Dark Sun Spring Run` profile.

### <img src="C:\Users\Marolok\IdeaProjects\vaadinTest\src\main\resources\static\img\icon\docker-icon.png" width="16"/> Build Docker image
1) Build production **.jar** - `mvn clean install -Pproduction`
2) Set DB path in [.env](./.env)
   1) `PC_DB_PATH` - your DB file
   2) `dbPath` - file in image
3) Build image - `docker build --no-cache -t marolok/dark_sun:1.0.0 .`
4) Set image version in [docker-compose.yml](./docker-compose.yml)
5) Start `docker-compose` - `docker-compose up` or `docker compose up`

## Problem and fix

1) NodeJS code `ERR_OSSL_EVP_UNSUPPORTED`
   * Problem: <img alt="cripto_problem" height="512" src="src/main/resources/static/img/readmeFiles/criptoProblems.png"/>
   * Solution: Set NodeJS v16:
     * `nvm install 16.13.1 64` (64 it is bit mode)
     * `nvm use 16.13.1`