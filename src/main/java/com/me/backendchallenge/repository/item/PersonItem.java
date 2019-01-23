package com.me.backendchallenge.repository.item;

import com.me.backendchallenge.model.Person;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "person")
public class PersonItem {

    @Id
    private String id;

    private String name;

    private String lastName;

    @Indexed(unique = true)
    private String document;

    private LocalDate birthDate;

    private String address;

    private List<String> phones;

    @Indexed(unique = true)
    private List<String> emails;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public PersonItem() {
    }

    public PersonItem(Person person) {
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
