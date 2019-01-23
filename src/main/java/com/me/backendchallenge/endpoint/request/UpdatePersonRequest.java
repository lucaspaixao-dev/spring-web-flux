package com.me.backendchallenge.endpoint.request;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.util.List;

public class UpdatePersonRequest extends PersonRequest {

    public UpdatePersonRequest() {

    }

    public UpdatePersonRequest(String id, String name, String lastName, String document,
                               LocalDate birthDate, String address, List<String> phones,
                               List<String> emails) {

        super(name, lastName, document, birthDate, address, phones, emails);
        this.id = id;
    }

    private String id;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("name", name)
                .append("lastName", lastName)
                .append("document", document)
                .append("birthDate", birthDate)
                .append("address", address)
                .append("phones", phones)
                .append("emails", emails)
                .toString();
    }
}
