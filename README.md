# Crud Spring WebFlux - API de Pessoas

Está API foi densenvolvida utilizando as stacks:
  - Java 11
  - Spring Web Flux
  - Gradle
  - MongoDB
  - Docker
  - Swagger

# Como Executar
Primeiramente certifique-se que você tenha instalado o [docker-compose](https://docs.docker.com/compose/gettingstarted/), execute o comando de build do docker-compose na raíz do projeto:
```sh
$ docker-compose build
```
Depois rode os containers:
```sh
$ docker-compose up
```
Para testar, realize um GET no endereço:
```sh
http://localhost:8182/api/v1/person/ 
```
A documentação se encontra na raiz do projeto com o nome doc.yml, para abrir copie o conteudo e cole no site do [swagger](https://editor.swagger.io/)
