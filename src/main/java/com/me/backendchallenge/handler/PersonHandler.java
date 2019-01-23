package com.me.backendchallenge.handler;

import com.me.backendchallenge.endpoint.request.PersonRequest;
import com.me.backendchallenge.endpoint.request.UpdatePersonRequest;
import com.me.backendchallenge.endpoint.response.PersonResponse;
import com.me.backendchallenge.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class PersonHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PersonHandler.class);

    private final PersonService service;

    @Autowired
    public PersonHandler(PersonService service) {
        this.service = service;
    }

    public Mono<ServerResponse> find(ServerRequest request) {
        var params = request.queryParams().toSingleValueMap();

        if (params.isEmpty()) {
            LOG.info("Solicitação para buscar todos os persons recebida.");

            return ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                    .body(service.listPersons().map(PersonResponse::new), PersonResponse.class);
        }

        var name = request.queryParam("name").orElse("");
        var lastName = request.queryParam("lastName").orElse("");
        var document = request.queryParam("document").orElse("0");

        LOG.info("Solicitação para buscar person com o name [{}], lastName [{}] e document [{}]",
                name, lastName, document);

        return ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(service.findUser(name, lastName, document).map(PersonResponse::new), PersonResponse.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(PersonRequest.class)
                .flatMap(service::newPerson)
                .map(PersonResponse::new)
                .flatMap(response -> status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(response), PersonResponse.class)
                        .doOnSuccess($ -> LOG.info("Novo person inserido com sucesso [{}]", response))
                );
    }

    public Mono<ServerResponse> saveManyPersons(ServerRequest request) {
        Flux<PersonResponse> responseFlux = request.bodyToFlux(PersonRequest.class)
                .flatMap(service::newPersons)
                .map(PersonResponse::new);

        return status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseFlux, PersonResponse.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return request.bodyToMono(UpdatePersonRequest.class)
                .flatMap(service::updatePerson)
                .map(PersonResponse::new)
                .flatMap(response -> ok().contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(response), PersonResponse.class)
                        .doOnSuccess($ -> LOG.info("Person alterado com sucesso [{}]", response))
                );
    }

    public Mono<ServerResponse> inactivate(ServerRequest request) {
        return service.inactivatePerson(request.pathVariable("id"))
                .flatMap(person -> noContent().build()
                        .doOnSuccess($ -> LOG.info("Person inativado com sucesso [{}]", person))
                )
                .switchIfEmpty(noContent().build());
    }

}