package com.me.backendchallenge.endpoint.response;

import com.me.backendchallenge.model.Person;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PersonResponse {

    private String id;
    private String name;
    private String lastName;
    private String document;
    private LocalDate birthDate;
    private String address;
    private List<String> phones;
    private List<String> emails;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PersonResponse() {

    }

    public PersonResponse(final Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.lastName = person.getLastName();
        this.document = person.getDocument();
        this.birthDate = person.getBirthDate();
        this.address = person.getAddress();
        this.phones = person.getPhones();
        this.emails = person.getEmails();
        this.active = person.getActive();
        this.createdAt = person.getCreatedAt();
        this.updatedAt = person.getUpdatedAt();
    }

    public String getId() {
        return id;
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

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
                .append("active", active)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .toString();
    }
}
