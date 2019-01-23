package com.me.backendchallenge.constants;

public class Constants {

    //path
    public static final String PATH = "/api/v1";
    public static final String PERSON_PATH = String.format("%s/person", PATH);

    //Validações
    public static final String NAME_IS_BLANK = "O campo nome não pode ser vázio.";
    public static final String LAST_NAME_IS_BLANK = "O campo sobrenome não pode ser vázio.";
    public static final String DOCUMENT_IS_NULL = "O campo CPF não pode ser nulo.";
    public static final String INVALID_DOCUMENT = "O CPF %s informado é inválido.";
    public static final String ADDRESS_IS_NULL = "O campo endereço não pode ser vázio.";
    public static final String BIRTH_DATE_IS_NULL = "O campo Data de nascimento não pode ser nulo.";
    public static final String INVALID_BIRTH_DATE = "O campo data de nascimento não pode ser maior que a data de hoje.";
    public static final String PHONES_IS_BLANK = "Os telefones não podem ser nulo.";
    public static final String EMAIL_IS_BLANK = "Os e-mails não podem ser nulo.";
    public static final String INVALID_EMAIL = "O e-mail %s informado é inválido.";
    public static final String ID_IS_BLANK = "O campo id não pode ser vázio.";
    public static final String INVALID_PHONE = "O telefone/celular %s informado é inválido.";

    public static String createErrorMessage(final String value, final String message) {
        return String.format(message, value);
    }
}
