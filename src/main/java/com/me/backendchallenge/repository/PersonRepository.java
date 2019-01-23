package com.me.backendchallenge.repository;

import com.me.backendchallenge.repository.item.PersonItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<PersonItem, String> {

    Mono<PersonItem> findByDocument(String document);

    Flux<PersonItem> findByNameIgnoreCase(String name);

    Flux<PersonItem> findByLastNameIgnoreCase(String lastName);

    Mono<PersonItem> findByEmails(String email);

}
