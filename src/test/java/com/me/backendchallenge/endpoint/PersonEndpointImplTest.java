package com.me.backendchallenge.endpoint;

import com.me.backendchallenge.endpoint.request.PersonRequest;
import com.me.backendchallenge.endpoint.request.UpdatePersonRequest;
import com.me.backendchallenge.endpoint.response.PersonResponse;
import com.me.backendchallenge.repository.PersonRepository;
import com.me.backendchallenge.constants.TestsConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.me.backendchallenge.constants.Constants.PATH;
import static com.me.backendchallenge.constants.Constants.PERSON_PATH;

@AutoConfigureWebTestClient(timeout = "36000")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class PersonEndpointImplTest {

    private static final String NEW_DOCUMENT = "89910684055";

    @Autowired
    private WebTestClient client;

    @Autowired
    private PersonRepository repository;

    @Nested
    class CreatePerson {

        @Test
        @DisplayName("Deve salvar uma nova pessoa e validar campos vázios e inválidos.")
        void test1() {
            repository.deleteAll().block();

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.name").isEqualTo(TestsConstants.NAME)
                    .jsonPath("$.lastName").isNotEmpty()
                    .jsonPath("$.lastName").isEqualTo(TestsConstants.LAST_NAME)
                    .jsonPath("$.document").isNotEmpty()
                    .jsonPath("$.document").isEqualTo(42536250881L)
                    .jsonPath("$.birthDate").isNotEmpty()
                    .jsonPath("$.birthDate").isEqualTo("1994-02-17")
                    .jsonPath("$.address").isNotEmpty()
                    .jsonPath("$.address").isEqualTo(TestsConstants.ADDRESS)
                    .jsonPath("$.phones").isNotEmpty()
                    .jsonPath("$.emails").isNotEmpty()
                    .jsonPath("$.active").isNotEmpty()
                    .jsonPath("$.active").isBoolean()
                    .jsonPath("$.createdAt").isNotEmpty()
                    .jsonPath("$.updatedAt").isNotEmpty();

            //CPF Duplicado.
            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("recurso_existente")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("CPF " + 42536250881L + " já cadastrado.");

            var requestEmail = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            //E-MAIL Duplicado.
            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(requestEmail), PersonRequest.class)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("recurso_existente")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("E-mail " + requestEmail.getEmails().get(0) + " já cadastrado.");

            //Nome vázio.
            var request2 = buildRequest("", TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request2), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O campo nome não pode ser vázio.");

            //Sobrenome vázio.
            var request3 = buildRequest(TestsConstants.NAME, "", NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request3), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O campo sobrenome não pode ser vázio.");

            //Documento inválido.
            var request4 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, "0", TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request4), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O CPF " + request4.getDocument() + " informado é inválido.");

            //Data de nascimento nula.
            var request5 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, null, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request5), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O campo Data de nascimento não pode ser nulo.");

            //Data de nascimento acima do dia de hoje.
            var request6 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, LocalDate.of(2020, 2, 17), TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request6), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O campo data de nascimento não pode ser maior que a data de hoje.");

            //Endereço vázio.
            var request7 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, "", buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request7), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O campo endereço não pode ser vázio.");

            //Lista de e-mails vázia.
            var request8 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, Collections.emptyList(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request8), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("Os e-mails não podem ser nulo.");

            //E-mail inválido.
            var request9 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, Collections.singletonList("lucasspxogmail.com"), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request9), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O e-mail " + request9.getEmails().get(0) + " informado é inválido.");

            //Lista de telefones vázia.
            var request10 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), Collections.emptyList());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request10), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("Os telefones não podem ser nulo.");

            //Telefone inválido.
            var request11 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), Collections.singletonList("1231232"));

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request11), PersonRequest.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("entrada_invalida")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("O telefone/celular " + request11.getPhones().get(0) + " informado é inválido.");
        }

    }

    @Nested
    class findPerson {

        @Test
        @DisplayName("Deve buscar todos as pessoas cadastradas e buscar pelo parametro informado na URL.")
        void test1() {
            repository.deleteAll().block();

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange();

            client.get()
                    .uri(PERSON_PATH)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.name").isEqualTo(TestsConstants.NAME)
                    .jsonPath("$.lastName").isNotEmpty()
                    .jsonPath("$.lastName").isEqualTo(TestsConstants.LAST_NAME)
                    .jsonPath("$.document").isNotEmpty()
                    .jsonPath("$.document").isEqualTo(42536250881L)
                    .jsonPath("$.birthDate").isNotEmpty()
                    .jsonPath("$.birthDate").isEqualTo("1994-02-17")
                    .jsonPath("$.address").isNotEmpty()
                    .jsonPath("$.address").isEqualTo(TestsConstants.ADDRESS)
                    .jsonPath("$.phones").isNotEmpty()
                    .jsonPath("$.emails").isNotEmpty()
                    .jsonPath("$.active").isNotEmpty()
                    .jsonPath("$.active").isBoolean()
                    .jsonPath("$.createdAt").isNotEmpty()
                    .jsonPath("$.updatedAt").isNotEmpty();

            client.get()
                    .uri(PERSON_PATH + "?document=" + TestsConstants.DOCUMENT)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.name").isEqualTo(TestsConstants.NAME)
                    .jsonPath("$.lastName").isNotEmpty()
                    .jsonPath("$.lastName").isEqualTo(TestsConstants.LAST_NAME)
                    .jsonPath("$.document").isNotEmpty()
                    .jsonPath("$.document").isEqualTo(42536250881L)
                    .jsonPath("$.birthDate").isNotEmpty()
                    .jsonPath("$.birthDate").isEqualTo("1994-02-17")
                    .jsonPath("$.address").isNotEmpty()
                    .jsonPath("$.address").isEqualTo(TestsConstants.ADDRESS)
                    .jsonPath("$.phones").isNotEmpty()
                    .jsonPath("$.emails").isNotEmpty()
                    .jsonPath("$.active").isNotEmpty()
                    .jsonPath("$.active").isBoolean()
                    .jsonPath("$.createdAt").isNotEmpty()
                    .jsonPath("$.updatedAt").isNotEmpty();

            client.get()
                    .uri(PERSON_PATH + "?document=1231231111")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .isEmpty();

            client.get()
                    .uri(PERSON_PATH + "?name=" + TestsConstants.NAME)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.name").isEqualTo(TestsConstants.NAME)
                    .jsonPath("$.lastName").isNotEmpty()
                    .jsonPath("$.lastName").isEqualTo(TestsConstants.LAST_NAME)
                    .jsonPath("$.document").isNotEmpty()
                    .jsonPath("$.document").isEqualTo(42536250881L)
                    .jsonPath("$.birthDate").isNotEmpty()
                    .jsonPath("$.birthDate").isEqualTo("1994-02-17")
                    .jsonPath("$.address").isNotEmpty()
                    .jsonPath("$.address").isEqualTo(TestsConstants.ADDRESS)
                    .jsonPath("$.phones").isNotEmpty()
                    .jsonPath("$.emails").isNotEmpty()
                    .jsonPath("$.active").isNotEmpty()
                    .jsonPath("$.active").isBoolean()
                    .jsonPath("$.createdAt").isNotEmpty()
                    .jsonPath("$.updatedAt").isNotEmpty();

            client.get()
                    .uri(PERSON_PATH + "?name=batatinha")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .isEmpty();

            client.get()
                    .uri(PERSON_PATH + "?lastName=" + TestsConstants.LAST_NAME)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.name").isEqualTo(TestsConstants.NAME)
                    .jsonPath("$.lastName").isNotEmpty()
                    .jsonPath("$.lastName").isEqualTo(TestsConstants.LAST_NAME)
                    .jsonPath("$.document").isNotEmpty()
                    .jsonPath("$.document").isEqualTo(42536250881L)
                    .jsonPath("$.birthDate").isNotEmpty()
                    .jsonPath("$.birthDate").isEqualTo("1994-02-17")
                    .jsonPath("$.address").isNotEmpty()
                    .jsonPath("$.address").isEqualTo(TestsConstants.ADDRESS)
                    .jsonPath("$.phones").isNotEmpty()
                    .jsonPath("$.emails").isNotEmpty()
                    .jsonPath("$.active").isNotEmpty()
                    .jsonPath("$.active").isBoolean()
                    .jsonPath("$.createdAt").isNotEmpty()
                    .jsonPath("$.updatedAt").isNotEmpty();

            client.get()
                    .uri(PERSON_PATH + "?name=batatinha Sobrenome")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .isEmpty();
        }

    }

    @Nested
    class InactivatePerson {

        @Test
        @DisplayName("Deve inativar a pessoa e retornar 204 quando inativar ou se não achar a pessoa.")
        void test1() {
            repository.deleteAll().block();

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange();

            var id = client.get()
                    .uri(PERSON_PATH)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .map(PersonResponse::getId)
                    .blockFirst();

            client.delete()
                    .uri(PERSON_PATH + "/" + id)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            client.delete()
                    .uri(PERSON_PATH + "/" + UUID.randomUUID().toString())
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();
        }

    }

    @Nested
    class CreateManyPersons {

        @Test
        @DisplayName("Deve inserir duas pessoas de três na primeira chamada e não inserir nenhuma na segunda chamada")
        void test1() {
            repository.deleteAll().block();

            List<PersonRequest> request = new ArrayList<>();

            request.add(buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones()));
            request.add(buildRequest("", TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones()));
            request.add(buildRequest("Raphael", "Silva", NEW_DOCUMENT, LocalDate.of(1990, 2, 10),
                    "Rua Teste", Collections.singletonList("raphael@gmail.com"), Collections.singletonList("16982532656")));

            client.post()
                    .uri(PATH + "/persons")
                    .body(Flux.fromStream(request.stream()), PersonRequest.class)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.[0].id").isNotEmpty()
                    .jsonPath("$.[0].name").isNotEmpty()
                    .jsonPath("$.[0].lastName").isNotEmpty()
                    .jsonPath("$.[0].document").isNotEmpty()
                    .jsonPath("$.[0].birthDate").isNotEmpty()
                    .jsonPath("$.[0].address").isNotEmpty()
                    .jsonPath("$.[0].phones").isNotEmpty()
                    .jsonPath("$.[0].emails").isNotEmpty()
                    .jsonPath("$.[0].active").isNotEmpty()
                    .jsonPath("$.[0].active").isBoolean()
                    .jsonPath("$.[0].createdAt").isNotEmpty()
                    .jsonPath("$.[0].updatedAt").isNotEmpty()
                    .jsonPath("$.[1].name").isNotEmpty()
                    .jsonPath("$.[1].lastName").isNotEmpty()
                    .jsonPath("$.[1].document").isNotEmpty()
                    .jsonPath("$.[1].birthDate").isNotEmpty()
                    .jsonPath("$.[1].address").isNotEmpty()
                    .jsonPath("$.[1].phones").isNotEmpty()
                    .jsonPath("$.[1].emails").isNotEmpty()
                    .jsonPath("$.[1].active").isNotEmpty()
                    .jsonPath("$.[1].active").isBoolean()
                    .jsonPath("$.[1].createdAt").isNotEmpty()
                    .jsonPath("$.[1].updatedAt").isNotEmpty();
        }
    }

    @Nested
    class UpdatePerson {

        @Test
        @DisplayName("Deve alterar a pessoa com o mesmo CPF.")
        void test1() {
            repository.deleteAll().block();

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange();

            var personResponse = client.get()
                    .uri(PERSON_PATH)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .blockFirst();

            var updateRequest = buildUpdatePersonRequest(personResponse.getId(), "José", "Manoel",
                    personResponse.getDocument(), personResponse.getBirthDate(), "Rua Teste", personResponse.getEmails(), personResponse.getPhones());

            client.put()
                    .uri(PERSON_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(updateRequest), UpdatePersonRequest.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.name").isEqualTo("José")
                    .jsonPath("$.lastName").isNotEmpty()
                    .jsonPath("$.lastName").isEqualTo("Manoel")
                    .jsonPath("$.document").isNotEmpty()
                    .jsonPath("$.document").isEqualTo(42536250881L)
                    .jsonPath("$.birthDate").isNotEmpty()
                    .jsonPath("$.birthDate").isEqualTo("1994-02-17")
                    .jsonPath("$.address").isNotEmpty()
                    .jsonPath("$.address").isEqualTo("Rua Teste")
                    .jsonPath("$.phones").isNotEmpty()
                    .jsonPath("$.emails").isNotEmpty()
                    .jsonPath("$.active").isNotEmpty()
                    .jsonPath("$.active").isBoolean()
                    .jsonPath("$.createdAt").isNotEmpty()
                    .jsonPath("$.updatedAt").isNotEmpty();
        }

        @Test
        @DisplayName("Deve retornar 409 quando enviar um CPF já cadastrado e diferente da pessoa.")
        void test2() {
            repository.deleteAll().block();

            PersonRequest request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange();

            var personResponse = client.get()
                    .uri(PERSON_PATH)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .blockFirst();

            PersonRequest request2 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, Collections.singletonList("teste@gmail.com"), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request2), PersonRequest.class)
                    .exchange();

            var personResponse2 = client.get()
                    .uri(PERSON_PATH + "?document=" + NEW_DOCUMENT)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .blockFirst();

            var updateRequest = buildUpdatePersonRequest(personResponse2.getId(), "José", "Manoel",
                    personResponse.getDocument(), personResponse2.getBirthDate(), "Rua Teste", personResponse2.getEmails(), personResponse2.getPhones());

            client.put()
                    .uri(PERSON_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(updateRequest), UpdatePersonRequest.class)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("recurso_existente")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("CPF " + personResponse.getDocument() + " já cadastrado.");
        }

        @Test
        @DisplayName("Deve retornar 404 quando não encontrar a pessoa.")
        void test3() {
            repository.deleteAll().block();

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange();

            var personResponse = client.get()
                    .uri(PERSON_PATH)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .blockFirst();

            var uuid = UUID.randomUUID().toString();

            var updateRequest = buildUpdatePersonRequest(uuid, "José", "Manoel",
                    NEW_DOCUMENT, personResponse.getBirthDate(), "Rua Teste", personResponse.getEmails(), personResponse.getPhones());

            client.put()
                    .uri(PERSON_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(updateRequest), UpdatePersonRequest.class)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("recurso_nao_encontrado")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("Pessoa com o identificador " + uuid + " não econtrada.");
        }

        @Test
        @DisplayName("Deve alterar a pessoa com o mesmo E-MAIL.")
        void test4() {
            repository.deleteAll().block();

            var request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange();

            var personResponse = client.get()
                    .uri(PERSON_PATH)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .blockFirst();

            var updateRequest = buildUpdatePersonRequest(personResponse.getId(), "José", "Manoel",
                    personResponse.getDocument(), personResponse.getBirthDate(), "Rua Teste", personResponse.getEmails(), personResponse.getPhones());

            client.put()
                    .uri(PERSON_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(updateRequest), UpdatePersonRequest.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.name").isEqualTo("José")
                    .jsonPath("$.lastName").isNotEmpty()
                    .jsonPath("$.lastName").isEqualTo("Manoel")
                    .jsonPath("$.document").isNotEmpty()
                    .jsonPath("$.document").isEqualTo(42536250881L)
                    .jsonPath("$.birthDate").isNotEmpty()
                    .jsonPath("$.birthDate").isEqualTo("1994-02-17")
                    .jsonPath("$.address").isNotEmpty()
                    .jsonPath("$.address").isEqualTo("Rua Teste")
                    .jsonPath("$.phones").isNotEmpty()
                    .jsonPath("$.emails").isNotEmpty()
                    .jsonPath("$.active").isNotEmpty()
                    .jsonPath("$.active").isBoolean()
                    .jsonPath("$.createdAt").isNotEmpty()
                    .jsonPath("$.updatedAt").isNotEmpty();
        }

        @Test
        @DisplayName("Deve retornar 409 quando enviar um E-MAIL já cadastrado e diferente da pessoa.")
        void test5() {
            repository.deleteAll().block();

            PersonRequest request = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, TestsConstants.DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, buildEmails(), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request), PersonRequest.class)
                    .exchange();

            client.get()
                    .uri(PERSON_PATH)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .blockFirst();

            PersonRequest request2 = buildRequest(TestsConstants.NAME, TestsConstants.LAST_NAME, NEW_DOCUMENT, TestsConstants.BIRTH_DATE, TestsConstants.ADDRESS, Collections.singletonList("teste@gmail.com"), buildPhones());

            client.post()
                    .uri(PERSON_PATH)
                    .body(Mono.just(request2), PersonRequest.class)
                    .exchange();

            var personResponse2 = client.get()
                    .uri(PERSON_PATH + "?document=" + NEW_DOCUMENT)
                    .exchange()
                    .returnResult(PersonResponse.class)
                    .getResponseBody()
                    .blockFirst();

            var updateRequest = buildUpdatePersonRequest(personResponse2.getId(), "José", "Manoel",
                    NEW_DOCUMENT, personResponse2.getBirthDate(), "Rua Teste", buildEmails(), personResponse2.getPhones());

            client.put()
                    .uri(PERSON_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(updateRequest), UpdatePersonRequest.class)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.error").isEqualTo("recurso_existente")
                    .jsonPath("$.error_description").isNotEmpty()
                    .jsonPath("$.error_description").isEqualTo("E-mail " + buildEmails().get(0) + " já cadastrado.");
        }
    }

    private UpdatePersonRequest buildUpdatePersonRequest(String id, String name, String lastName, String document, LocalDate birthDate,
                                                         String address, List<String> emails, List<String> phones) {

        return new UpdatePersonRequest(id, name,
                lastName, document, birthDate, address, phones, emails);
    }

    private PersonRequest buildRequest(String name, String lastName, String document, LocalDate birthDate,
                                       String address, List<String> emails, List<String> phones) {

        return new PersonRequest(name, lastName, document, birthDate, address, phones, emails);
    }

    private List<String> buildEmails() {
        return Collections.singletonList(TestsConstants.E_MAIL);
    }

    private List<String> buildPhones() {
        return Collections.singletonList(TestsConstants.PHONE);
    }
}
