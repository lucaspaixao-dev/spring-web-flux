package com.me.backendchallenge.service;

import com.me.backendchallenge.endpoint.request.PersonRequest;
import com.me.backendchallenge.endpoint.request.UpdatePersonRequest;
import com.me.backendchallenge.model.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonService {

    Flux<Person> listPersons();

    Flux<Person> findUser(String name, String lastName, String document);

    Mono<Person> newPerson(PersonRequest person);

    Mono<Person> newPersons(final PersonRequest personRequest);

    Mono<Person> updatePerson(UpdatePersonRequest request);

    Mono<Person> inactivatePerson(String id);

}
