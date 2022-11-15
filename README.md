# **Build and run**
![Dar Sun screen](src/main/resources/static/img/readmeFiles/dar_sun_screen.png)
1) Install JDK8 - https://adoptopenjdk.net/
2) Install NodeJS - https://nodejs.org/en/download/
   * Install nvm - [Windows](https://github.com/coreybutler/nvm-windows), [Linux](https://github.com/nvm-sh/nvm)
3) In project dir run command `npm install`
4) In `Dark Sun Spring Run` edit `Environment variables` - set `dbPath` (path to SQLite db file).
5) Start `Dark Sun Spring Run` profile.

### Problem and fix

1) NodeJS code `ERR_OSSL_EVP_UNSUPPORTED`
   * Problem: ![cripto_problem](src/main/resources/static/img/readmeFiles/criptoProblems.png)
   * Solution: Set NodeJS v16:
     * `nvm install 16.13.1 64` (64 it is bit mode)
     * `nvm use 16.13.1`