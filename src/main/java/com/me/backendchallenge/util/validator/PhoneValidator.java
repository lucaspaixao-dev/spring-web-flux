package com.me.backendchallenge.util.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator {

    private static final String REGION = "(\\d{2})(\\d{8,9})";

    public boolean isValid(final String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(REGION);
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches();
    }
}
