package com.me.backendchallenge.config;

import com.me.backendchallenge.exceptions.InternalServerErrorException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

@Configuration
public class ApplicationConfig {

    @Bean
    public DataBufferFactory dataBufferFactory() {
        return new DefaultDataBufferFactory();
    }

    @Bean
    public InternalServerErrorException errorException() {
        return new InternalServerErrorException();
    }

    @Bean
    public ObjectMapper jacksonConfig() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
