package com.me.backendchallenge.service.impl;

import com.me.backendchallenge.endpoint.request.PersonRequest;
import com.me.backendchallenge.endpoint.request.UpdatePersonRequest;
import com.me.backendchallenge.exceptions.BadRequestException;
import com.me.backendchallenge.exceptions.ConflictException;
import com.me.backendchallenge.exceptions.NotFoundException;
import com.me.backendchallenge.model.Person;
import com.me.backendchallenge.repository.PersonRepository;
import com.me.backendchallenge.repository.item.PersonItem;
import com.me.backendchallenge.constants.TestsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PersonServiceImplTest {

    @Mock
    private PersonRepository repository;

    @InjectMocks
    private PersonServiceImpl service;


    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    class CreatePerson {

        @Test
        @DisplayName("Deve salvar uma nova pessoa com sucesso.")
        void test1() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.findByEmails(anyString())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            var person = service.newPerson(request).block();

            assertNotNull(person);

            assertNotNull(person.getName());
            assertEquals(TestsConstants.NAME, person.getName());

            assertNotNull(person.getLastName());
            assertEquals(TestsConstants.LAST_NAME, person.getLastName());

            assertNotNull(person.getDocument());
            assertEquals(TestsConstants.DOCUMENT, person.getDocument());

            assertNotNull(person.getBirthDate());
            assertEquals(TestsConstants.BIRTH_DATE, person.getBirthDate());

            assertNotNull(person.getAddress());
            assertEquals(TestsConstants.ADDRESS, person.getAddress());

            assertNotNull(person.getPhones());
            assertEquals(buildPhones(), person.getPhones());

            assertNotNull(person.getActive());
            assertTrue(person.getActive());

            assertNotNull(person.getCreatedAt());
            assertNotNull(person.getUpdatedAt());
        }

        @Test
        @DisplayName("Deve retornar 409 quando já existe um CPF cadastrado com o mesmo número que o da request.")
        void test2() {
            when(repository.findByDocument(any())).thenReturn(Mono.just(buildItem()));

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            assertThrows(ConflictException.class, () -> service.newPerson(request).block());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o nome for vázio.")
        void test3() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request = buildRequest("", TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            assertThrows(BadRequestException.class, () -> service.newPerson(request).block());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o sobrenome for vázio.")
        void test4() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var emails = buildEmails();
            var phones = buildPhones();

            var request = new PersonRequest(TestsConstants.NAME, "", TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, phones, emails);

            assertThrows(BadRequestException.class, () -> service.newPerson(request).block());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o CPF for vázio e inválido.")
        void test5() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var emails = buildEmails();
            var phones = buildPhones();

            var request1 = new PersonRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, null, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, phones, emails);

            assertThrows(BadRequestException.class, () -> service.newPerson(request1).block());

            var request2 = new PersonRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, "123231231123", TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, phones, emails);

            assertThrows(BadRequestException.class, () -> service.newPerson(request2).block());
        }

        @Test
        @DisplayName("Deve retornar 400 quando a data de nascimento for vázio e acima da data atual.")
        void test6() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var emails = buildEmails();
            var phones = buildPhones();

            var request1 = new PersonRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, null, TestsConstants.ADDRESS, phones, emails);

            assertThrows(BadRequestException.class, () -> service.newPerson(request1).block());

            var request2 = new PersonRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, LocalDate.of(2025, 2, 17), TestsConstants.ADDRESS, phones, emails);

            assertThrows(BadRequestException.class, () -> service.newPerson(request2).block());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o endereço for vázio.")
        void test7() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, "", buildEmails(), buildPhones());

            assertThrows(BadRequestException.class, () -> service.newPerson(request).block());
        }

        @Test
        @DisplayName("Deve retornar 400 quando a lista de telefones for vázio e algum inválido.")
        void test8() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request1 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), Collections.emptyList());

            assertThrows(BadRequestException.class, () -> service.newPerson(request1).block());

            var request2 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), Collections.singletonList("111233"));

            assertThrows(BadRequestException.class, () -> service.newPerson(request2).block());
        }

        @Test
        @DisplayName("Deve retornar 400 quando a lista de e-mails for vázio e algum inválido.")
        void test9() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request1 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, Collections.emptyList(), buildPhones());

            assertThrows(BadRequestException.class, () -> service.newPerson(request1).block());

            var request2 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, Collections.singletonList("lucasgmail.com"), buildPhones());

            assertThrows(BadRequestException.class, () -> service.newPerson(request2).block());
        }

    }

    @Nested
    class FindPerson {

        @Test
        @DisplayName("Deve retornar a pessoa quando o CPF for válido e não retornar quando for inválido ou vázio, com os outros parametros vázios.")
        void test1() {
            var person = buildPerson();

            when(repository.findByDocument(any())).thenReturn(Mono.just(new PersonItem(person)));

            var personResponse = service.findUser("", "", TestsConstants.DOCUMENT).blockFirst();

            assertNotNull(personResponse);

            assertNotNull(personResponse.getName());
            assertEquals(TestsConstants.NAME, personResponse.getName());

            assertNotNull(personResponse.getLastName());
            assertEquals(TestsConstants.LAST_NAME, personResponse.getLastName());

            assertNotNull(personResponse.getDocument());
            assertEquals(TestsConstants.DOCUMENT, personResponse.getDocument());

            assertNotNull(personResponse.getBirthDate());
            assertEquals(TestsConstants.BIRTH_DATE, personResponse.getBirthDate());

            assertNotNull(personResponse.getAddress());
            assertEquals(TestsConstants.ADDRESS, personResponse.getAddress());

            assertNotNull(personResponse.getPhones());
            assertEquals(buildPhones(), personResponse.getPhones());

            assertNotNull(personResponse.getActive());
            assertTrue(personResponse.getActive());

            assertNotNull(personResponse.getCreatedAt());
            assertNotNull(personResponse.getUpdatedAt());
            assertNotNull(personResponse.getId());

            var personResponse2 = service.findUser("", "", "12312312313213").blockFirst();

            assertNull(personResponse2);
        }

        @Test
        @DisplayName("Deve retornar a pessoa quando o nome for preenchido e os outros atributos não.")
        void test2() {
            var person = buildPerson();

            when(repository.findByNameIgnoreCase(any())).thenReturn(Flux.just(new PersonItem(person)));

            var personResponse = service.findUser(TestsConstants.NAME, "", "0").blockFirst();

            assertNotNull(personResponse);

            assertNotNull(personResponse.getName());
            assertEquals(TestsConstants.NAME, personResponse.getName());

            assertNotNull(personResponse.getLastName());
            assertEquals(TestsConstants.LAST_NAME, personResponse.getLastName());

            assertNotNull(personResponse.getDocument());
            assertEquals(TestsConstants.DOCUMENT, personResponse.getDocument());

            assertNotNull(personResponse.getBirthDate());
            assertEquals(TestsConstants.BIRTH_DATE, personResponse.getBirthDate());

            assertNotNull(personResponse.getAddress());
            assertEquals(TestsConstants.ADDRESS, personResponse.getAddress());

            assertNotNull(personResponse.getPhones());
            assertEquals(buildPhones(), personResponse.getPhones());

            assertNotNull(personResponse.getActive());
            assertTrue(personResponse.getActive());

            assertNotNull(personResponse.getCreatedAt());
            assertNotNull(personResponse.getUpdatedAt());
            assertNotNull(personResponse.getId());
        }

        @Test
        @DisplayName("Deve retornar a pessoa quando o sobrenome for preenchido e os outros atributos não.")
        void test3() {
            var person = buildPerson();

            when(repository.findByLastNameIgnoreCase(any())).thenReturn(Flux.just(new PersonItem(person)));

            var personResponse = service.findUser("", TestsConstants.LAST_NAME, "0").blockFirst();

            assertNotNull(personResponse);

            assertNotNull(personResponse.getName());
            assertEquals(TestsConstants.NAME, personResponse.getName());

            assertNotNull(personResponse.getLastName());
            assertEquals(TestsConstants.LAST_NAME, personResponse.getLastName());

            assertNotNull(personResponse.getDocument());
            assertEquals(TestsConstants.DOCUMENT, personResponse.getDocument());

            assertNotNull(personResponse.getBirthDate());
            assertEquals(TestsConstants.BIRTH_DATE, personResponse.getBirthDate());

            assertNotNull(personResponse.getAddress());
            assertEquals(TestsConstants.ADDRESS, personResponse.getAddress());

            assertNotNull(personResponse.getPhones());
            assertEquals(buildPhones(), personResponse.getPhones());

            assertNotNull(personResponse.getActive());
            assertTrue(personResponse.getActive());

            assertNotNull(personResponse.getCreatedAt());

            assertNotNull(personResponse.getUpdatedAt());

            assertNotNull(personResponse.getId());
        }

        @Test
        @DisplayName("Não deve retornar nenhum resultado quando nenhum parametro de pesquisa for passado.")
        void test4() {
            assertNull(service.findUser("", "", "0").blockFirst());
        }

    }

    @Nested
    class InactivatePerson {

        @Test
        @DisplayName("Deve invativar a pessoa.")
        void test1() {
            var person = buildPerson();

            when(repository.findById(anyString())).thenReturn(Mono.just(new PersonItem(person)));

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            Person inactivePerson = service.inactivatePerson(UUID.randomUUID().toString()).block();

            assertNotNull(inactivePerson);

            assertFalse(inactivePerson.getActive());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o parametro id não for passado.")
        void test2() {
            when(repository.findById(anyString())).thenReturn(Mono.empty());

            assertThrows(BadRequestException.class, () -> service.inactivatePerson(null).block());
        }

        @Test
        @DisplayName("Deve retornar vázio quando não achar a pessoa com o id fornecido.")
        void test3() {
            when(repository.findById(anyString())).thenReturn(Mono.empty());

            assertNull(service.inactivatePerson(UUID.randomUUID().toString()).block());
        }

    }

    @Nested
    class UpdatePerson {

        @Test
        @DisplayName("Deve retornar 409 quando existir um CPF cadastrado igual ao da requisição.")
        void test1() {
            var person = buildPerson();

            when(repository.findById(anyString())).thenReturn(Mono.just(new PersonItem(person)));

            when(repository.findByDocument(any())).thenReturn(Mono.just(new PersonItem(person)));

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request = buildUpdatePersonRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            assertThrows(ConflictException.class, () -> service.updatePerson(request).block());
        }

        @Test
        @DisplayName("Deve retornar 404 quando não encontrar a pessoa para realizar a alteração.")
        void test2() {
            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.findById(anyString())).thenReturn(Mono.empty());

            var request = buildUpdatePersonRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            assertThrows(NotFoundException.class, () -> service.updatePerson(request).block());
        }

        @Test
        @DisplayName("Deve alterar a pessoa.")
        void test3() {
            var person = buildPerson();

            when(repository.findByDocument(any())).thenReturn(Mono.empty());

            when(repository.findById(anyString())).thenReturn(Mono.just(new PersonItem(person)));

            when(repository.findByEmails(anyString())).thenReturn(Mono.empty());

            when(repository.save(any(PersonItem.class)))
                    .thenAnswer((Answer) invocation -> Mono.just(invocation.getArguments()[0]));

            var request = buildUpdatePersonRequest("Manoel", "Silva", TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, "Av. Paulista", buildEmails(), buildPhones());

            var personResponse = service.updatePerson(request).block();

            assertNotNull(personResponse);

            assertNotNull(personResponse.getName());
            assertEquals(request.getName(), personResponse.getName());

            assertNotNull(personResponse.getLastName());
            assertEquals(request.getLastName(), personResponse.getLastName());

            assertNotNull(personResponse.getDocument());
            assertEquals(TestsConstants.DOCUMENT, personResponse.getDocument());

            assertNotNull(personResponse.getBirthDate());
            assertEquals(TestsConstants.BIRTH_DATE, personResponse.getBirthDate());

            assertNotNull(personResponse.getAddress());
            assertEquals(request.getAddress(), personResponse.getAddress());

            assertNotNull(personResponse.getPhones());
            assertEquals(buildPhones(), personResponse.getPhones());

            assertNotNull(personResponse.getActive());
            assertTrue(personResponse.getActive());

            assertNotNull(personResponse.getCreatedAt());
            assertNotNull(personResponse.getUpdatedAt());
            assertNotNull(personResponse.getId());
        }

    }

    private PersonRequest buildRequest(String name, String lastName, String document, LocalDate birthDate,
                                       String address, List<String> emails, List<String> phones) {

        return new PersonRequest(name, lastName, document, birthDate, address, phones, emails);
    }

    private UpdatePersonRequest buildUpdatePersonRequest(String name, String lastName, String document, LocalDate birthDate,
                                                         String address, List<String> emails, List<String> phones) {

        return new UpdatePersonRequest(UUID.randomUUID().toString(), name,
                lastName, document, birthDate, address, phones, emails);
    }

    private List<String> buildEmails() {
        return Collections.singletonList(TestsConstants.E_MAIL);
    }

    private List<String> buildPhones() {
        return Collections.singletonList(TestsConstants.PHONE);
    }

    private PersonItem buildItem() {
        return new PersonItem();
    }

    private Person buildPerson() {
        return new Person.Builder()
                .withId(UUID.randomUUID().toString())
                .withName(TestsConstants.NAME)
                .withLastName(TestsConstants.LAST_NAME)
                .withDocument(TestsConstants.DOCUMENT)
                .withBirthDate(TestsConstants.BIRTH_DATE)
                .withAddress(TestsConstants.ADDRESS)
                .withPhones(buildPhones())
                .withEmails(buildEmails())
                .withActive(true)
                .withCreatedAt(LocalDateTime.now())
                .withUpdatedAt()
                .build();
    }

}
