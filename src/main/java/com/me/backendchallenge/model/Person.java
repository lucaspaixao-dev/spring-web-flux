package com.me.backendchallenge.model;

import com.me.backendchallenge.exceptions.BadRequestException;
import com.me.backendchallenge.repository.item.PersonItem;
import com.me.backendchallenge.constants.Constants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.me.backendchallenge.util.ValidatorUtil.*;

public class Person {

    private final String id;
    private final String name;
    private final String lastName;
    private final String document;
    private final LocalDate birthDate;
    private final String address;
    private final List<String> phones;
    private final List<String> emails;
    private final Boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Person(PersonItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.lastName = item.getLastName();
        this.document = item.getDocument();
        this.birthDate = item.getBirthDate();
        this.address = item.getAddress();
        this.phones = item.getPhones();
        this.emails = item.getEmails();
        this.active = item.getActive();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }

    private Person(Person.Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.document = builder.document;
        this.birthDate = builder.birthDate;
        this.address = builder.address;
        this.phones = builder.phones;
        this.emails = builder.emails;
        this.active = builder.active;
        this.updatedAt = builder.updatedAt;
        this.createdAt = builder.createdAt;
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

    public Person inactivate() {
        return new Builder()
                .withId(id)
                .withName(name)
                .withLastName(lastName)
                .withDocument(document)
                .withBirthDate(birthDate)
                .withAddress(address)
                .withPhones(phones)
                .withEmails(emails)
                .withActive(false)
                .withCreatedAt(createdAt)
                .build();
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

    public static class Builder {
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

        public Person.Builder withId(String id) {
            if (isBlank(id)) {
                this.id = UUID.randomUUID().toString();
            }

            this.id = id;
            return this;
        }

        public Person.Builder withName(String name) {
            if (isBlank(name)) {
                throw new BadRequestException(Constants.NAME_IS_BLANK);
            }

            this.name = name;
            return this;
        }

        public Person.Builder withLastName(String lastName) {
            if (isBlank(lastName)) {
                throw new BadRequestException(Constants.LAST_NAME_IS_BLANK);
            }

            this.lastName = lastName;
            return this;
        }

        public Person.Builder withDocument(String document) {
            if (isNull(document)) {
                throw new BadRequestException(Constants.DOCUMENT_IS_NULL);
            }

            if (!validateDocument(document)) {
                throw new BadRequestException(Constants.createErrorMessage(document, Constants.INVALID_DOCUMENT));
            }

            this.document = document;
            return this;
        }

        public Person.Builder withBirthDate(LocalDate birthDate) {
            if (isNull(birthDate)) {
                throw new BadRequestException(Constants.BIRTH_DATE_IS_NULL);
            }

            if (isFuture(birthDate)) {
                throw new BadRequestException(Constants.INVALID_BIRTH_DATE);
            }

            this.birthDate = birthDate;
            return this;
        }

        public Person.Builder withAddress(String address) {
            if (isBlank(address)) {
                throw new BadRequestException(Constants.ADDRESS_IS_NULL);
            }

            this.address = address;
            return this;
        }

        public Person.Builder withPhones(List<String> phones) {
            if (isEmpty(phones)) {
                throw new BadRequestException(Constants.PHONES_IS_BLANK);
            }

            this.phones = phones.stream()
                    .peek(phone -> {
                        if (!isValidPhone(phone)) {
                            throw new BadRequestException(Constants.createErrorMessage(phone, Constants.INVALID_PHONE));
                        }
                    }).collect(Collectors.toList());

            return this;
        }

        public Person.Builder withEmails(List<String> emails) {
            if (isEmpty(emails)) {
                throw new BadRequestException(Constants.EMAIL_IS_BLANK);
            }

            this.emails = emails.stream()
                    .peek(email -> {
                        if (!validateEmail(email)) {
                            throw new BadRequestException(Constants.createErrorMessage(email, Constants.INVALID_EMAIL));
                        }

                    }).collect(Collectors.toList());

            return this;
        }

        public Person.Builder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public Person.Builder withUpdatedAt() {
            this.updatedAt = LocalDateTime.now();
            return this;
        }

        public Person.Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Person build() {
            if (isBlank(this.id)) {
                this.id = UUID.randomUUID().toString();
            }

            if (this.active == null) {
                this.active = true;
            }

            var now = LocalDateTime.now();

            this.updatedAt = now;

            if (this.createdAt == null) {
                this.createdAt = now;
            }

            return new Person(this);
        }
    }
}
