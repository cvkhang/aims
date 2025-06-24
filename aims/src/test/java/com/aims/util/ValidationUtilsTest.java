package com.aims.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {
    @Test
    void testPhoneNumberValidation() {
        assertTrue(ValidationUtils.isValidPhoneNumber("0123456789"));
        assertFalse(ValidationUtils.isValidPhoneNumber("1234"));
        assertFalse(ValidationUtils.isValidPhoneNumber("abc123"));
        assertFalse(ValidationUtils.isValidPhoneNumber("1234567890"));
    }

    @Test
    void testNameValidation() {
        assertTrue(ValidationUtils.isValidName("chuvinhkhang"));
        assertFalse(ValidationUtils.isValidName("khang1234"));
        assertFalse(ValidationUtils.isValidName("#?Khang"));
        assertTrue(ValidationUtils.isValidName("Chu Vinh Khang"));
        assertTrue(ValidationUtils.isValidName("ChuVinhKhang"));
        assertFalse(ValidationUtils.isValidName(null));
        assertFalse(ValidationUtils.isValidName(""));
    }

    @Test
    void testAddressValidation() {
        assertTrue(ValidationUtils.isValidAddress("hanoi"));
        assertTrue(ValidationUtils.isValidAddress("so 1 Dai Co VIet Hanoi"));
        assertFalse(ValidationUtils.isValidAddress("$# Hanoi"));
        assertTrue(ValidationUtils.isValidAddress("dai co viet Ha Noi"));
        assertFalse(ValidationUtils.isValidAddress(null));
        assertFalse(ValidationUtils.isValidAddress(""));
    }
}
