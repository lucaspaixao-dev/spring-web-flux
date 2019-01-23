package com.me.backendchallenge.exceptions.handler;

import com.me.backendchallenge.exceptions.ApplicationException;
import com.me.backendchallenge.exceptions.InternalServerErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * Está classe é um interceptor que realiza a customização das exceptions, setando o httpStatus e retornando um JSON no
 * seguinte padrão:
 * <p>
 * {
 * "error": "",
 * "error_description": ""
 * }
 */
@Component
@Order(-2)
public class ExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper mapper;
    private final DataBufferFactory dataBufferFactory;
    private final InternalServerErrorException errorException;

    @Autowired
    public ExceptionHandler(ObjectMapper mapper, DataBufferFactory dataBufferFactory, InternalServerErrorException errorException) {
        this.mapper = mapper;
        this.dataBufferFactory = dataBufferFactory;
        this.errorException = errorException;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ExceptionParserInterface parser = (application) -> {
            exchange.getResponse().setStatusCode(HttpStatus.resolve(application.getHttpCode()));
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            var body = Mono.just(dataBufferFactory.wrap(mapper.writeValueAsBytes(application)));

            return exchange.getResponse().writeWith(body);
        };

        try {
            return Mono.from(parser.parse((ApplicationException) ex)); //TODO ajustar exception genericas.

        } catch (JsonProcessingException e) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();

        }
    }
}
