package com.me.backendchallenge.util;

import br.com.caelum.stella.validation.CPFValidator;
import com.me.backendchallenge.util.validator.PhoneValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDate;
import java.util.List;

public class ValidatorUtil {

    public static boolean validateDocument(final String document) {
        try {
            new CPFValidator().assertValid(document);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateEmail(final String email) {
        return EmailValidator.getInstance()
                .isValid(email);
    }

    public static boolean isBlank(final String word) {
        return StringUtils.isBlank(word);
    }

    public static boolean isNull(final Object object) {
        return object == null;
    }

    public static boolean isEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isFuture(final LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    public static boolean isValidPhone(final String phone) {
        PhoneValidator validator = new PhoneValidator();
        return validator.isValid(phone);
    }
}
