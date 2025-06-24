package com.aims.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z](?:[A-Za-z ]*[A-Za-z])?$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9](?:[A-Za-z0-9 ]*[A-Za-z0-9])?$");

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        return NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidAddress(String address) {
        if (address == null) return false;
        return ADDRESS_PATTERN.matcher(address).matches();
    }
}
