package com.me.backendchallenge.endpoint.request;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.util.List;

public class PersonRequest {

    protected String name;
    protected String lastName;
    protected String document;
    protected LocalDate birthDate;
    protected String address;
    protected List<String> phones;
    protected List<String> emails;

    public PersonRequest() {

    }

    public PersonRequest(String name, String lastName, String document,
                         LocalDate birthDate, String address, List<String> phones,
                         List<String> emails) {

        this.name = name;
        this.lastName = lastName;
        this.document = document;
        this.birthDate = birthDate;
        this.address = address;
        this.phones = phones;
        this.emails = emails;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocument() {
        return document;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getPhones() {
        return phones;
    }

    public List<String> getEmails() {
        return emails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
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
