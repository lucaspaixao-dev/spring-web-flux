package com.me.backendchallenge.endpoint;

import com.me.backendchallenge.handler.PersonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.me.backendchallenge.constants.Constants.PATH;
import static com.me.backendchallenge.constants.Constants.PERSON_PATH;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PersonEndpoint {

    @Bean
    RouterFunction<ServerResponse> routes(PersonHandler handler) {
        return route()
                .path(PERSON_PATH, builder -> builder
                        .GET("", handler::find)

                        .nest(accept(MediaType.APPLICATION_JSON), b2 -> b2
                                .POST("", handler::save)
                                .PUT("", handler::update))

                        .DELETE("/{id}", handler::inactivate))

                .path(PATH + "/persons", builder -> builder
                        .nest(accept(MediaType.APPLICATION_JSON), b2 -> b2
                                .POST("", handler::saveManyPersons)))

                .build();
    }
}
