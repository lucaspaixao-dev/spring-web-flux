package com.me.backendchallenge.exceptions.handler;

import com.me.backendchallenge.exceptions.ApplicationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.reactivestreams.Publisher;

public interface ExceptionParserInterface {

    Publisher<Void> parse(ApplicationException ex) throws JsonProcessingException;
}
