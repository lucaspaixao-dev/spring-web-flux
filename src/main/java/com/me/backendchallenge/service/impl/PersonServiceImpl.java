package com.me.backendchallenge.service.impl;

import com.me.backendchallenge.constants.Constants;
import com.me.backendchallenge.endpoint.request.PersonRequest;
import com.me.backendchallenge.endpoint.request.UpdatePersonRequest;
import com.me.backendchallenge.exceptions.BadRequestException;
import com.me.backendchallenge.exceptions.ConflictException;
import com.me.backendchallenge.exceptions.NotFoundException;
import com.me.backendchallenge.model.Person;
import com.me.backendchallenge.repository.PersonRepository;
import com.me.backendchallenge.repository.item.PersonItem;
import com.me.backendchallenge.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.me.backendchallenge.util.ValidatorUtil.*;
import static java.lang.String.format;

@Service
public class PersonServiceImpl implements PersonService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepository repository;

    @Autowired
    public PersonServiceImpl(PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<Person> listPersons() {
        return repository.findAll()
                .map(Person::new);
    }

    @Override
    public Flux<Person> findUser(final String name, final String lastName, final String document) {
        if (!isNull(document) && validateDocument(document)) {
            return Flux.from(findByDocument(document));
        }

        if (!isBlank(name)) {
            return findByName(name);
        }

        if (!isBlank(lastName)) {
            return findByLastName(lastName);
        }

        return Flux.empty();
    }

    @Override
    public Mono<Person> newPerson(final PersonRequest personRequest) {
        LOG.info("Validando person para inserção [{}]", personRequest);

        return buildPerson(personRequest).flatMap(person ->
                findByDocument(person.getDocument())
                        .flatMap(p -> Mono.error(new ConflictException(format("CPF %s já cadastrado.", personRequest.getDocument()))))

                        .then(Flux.fromStream(person.getEmails().stream())
                                .flatMap(this::checkDuplicateEmail)
                                .then())

                        .then(save(person))
                        .map(Person::new));
    }

    @Override
    public Mono<Person> newPersons(final PersonRequest personRequest) {
        return newPerson(personRequest).onErrorResume($ -> Mono.empty());
    }

    @Override
    public Mono<Person> updatePerson(final UpdatePersonRequest personRequest) {
        LOG.info("Validando person para alteração [{}]", personRequest);

        return checkId(personRequest.getId())
                .switchIfEmpty(Mono.error(new NotFoundException(format("Pessoa com o identificador %s não econtrada.", personRequest.getId()))))
                .flatMap(person -> build(person, personRequest)
                        .flatMap(personUpdated -> findByDocument(personRequest.getDocument())
                                .flatMap(existPerson -> validateDocumentUpdate(existPerson, personRequest))

                                .then(Flux.fromStream(personUpdated.getEmails().stream())
                                        .flatMap(email -> checkDuplicateEmail(email, personUpdated))
                                        .then())

                                .then(this.save(personUpdated))
                                .map(Person::new)));
    }

    @Override
    public Mono<Person> inactivatePerson(final String id) {
        LOG.info("Validando person para inativação com o id [{}]", id);

        return checkId(id)
                .switchIfEmpty(Mono.empty())
                .map(Person::inactivate)
                .flatMap(this::save)
                .map(Person::new);
    }

    private Person build(PersonRequest personRequest) {
        return new Person.Builder()
                .withName(personRequest.getName())
                .withLastName(personRequest.getLastName())
                .withDocument(personRequest.getDocument())
                .withBirthDate(personRequest.getBirthDate())
                .withAddress(personRequest.getAddress())
                .withPhones(personRequest.getPhones())
                .withEmails(personRequest.getEmails())
                .build();
    }

    private Mono<Person> build(Person person, UpdatePersonRequest personRequest) {
        return Mono.just(new Person.Builder()
                .withId(person.getId())
                .withName(personRequest.getName())
                .withLastName(personRequest.getLastName())
                .withDocument(personRequest.getDocument())
                .withBirthDate(personRequest.getBirthDate())
                .withAddress(personRequest.getAddress())
                .withPhones(personRequest.getPhones())
                .withEmails(personRequest.getEmails())
                .withActive(person.getActive())
                .withUpdatedAt()
                .withCreatedAt(person.getCreatedAt())
                .build());
    }

    private Mono<Void> validateDocumentUpdate(Person existPerson, UpdatePersonRequest personRequest) {
        return !existPerson.getId().equals(personRequest.getId()) ?
                Mono.error(new ConflictException(format("CPF %s já cadastrado.", personRequest.getDocument()))) :
                Mono.empty();
    }

    private Mono<Person> buildPerson(PersonRequest personRequest) {
        return Mono.fromCallable(() -> build(personRequest));
    }

    private Mono<Person> checkId(final String id) {
        return Optional.ofNullable(id)
                .map(this::findById)
                .orElseThrow(() -> new BadRequestException(Constants.ID_IS_BLANK));
    }

    private Mono<PersonItem> save(final Person person) {
        return repository.save(new PersonItem(person));
    }

    private Mono<Person> findById(final String id) {
        return repository.findById(id)
                .map(Person::new);
    }

    private Mono<Person> findByDocument(final String document) {
        return repository.findByDocument(document)
                .map(Person::new);
    }

    private Flux<Person> findByName(final String name) {
        return repository.findByNameIgnoreCase(name)
                .map(Person::new);
    }

    private Flux<Person> findByLastName(final String lastName) {
        return repository.findByLastNameIgnoreCase(lastName)
                .map(Person::new);
    }

    private Mono<Person> findByEmail(final String email) {
        return repository.findByEmails(email)
                .map(Person::new);
    }

    private Mono<Void> checkDuplicateEmail(String email) {
        return findByEmail(email)
                .switchIfEmpty(Mono.empty())
                .flatMap(person -> Mono.error(new ConflictException(format("E-mail %s já cadastrado.", email))));
    }

    private Mono<Void> checkDuplicateEmail(String email, Person person) {
        return findByEmail(email)
                .switchIfEmpty(Mono.empty())
                .flatMap(p -> {
                    if (!p.getId().equals(person.getId())) {
                        return Mono.error(new ConflictException(format("E-mail %s já cadastrado.", email)));
                    }
                    return Mono.empty();
                });
    }
}
