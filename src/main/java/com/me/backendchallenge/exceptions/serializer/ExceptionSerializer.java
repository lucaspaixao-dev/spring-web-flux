package com.me.backendchallenge.exceptions.serializer;

import com.me.backendchallenge.exceptions.ApplicationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ExceptionSerializer extends StdSerializer<ApplicationException> {

    public ExceptionSerializer() {
        this(null);
    }

    private ExceptionSerializer(Class<ApplicationException> t) {
        super(t);
    }

    @Override
    public void serialize(ApplicationException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("error", value.getErrorCode());

        if (value.getMessage() != null) {
            gen.writeStringField("error_description", value.getMessage());
        }
    }
}
