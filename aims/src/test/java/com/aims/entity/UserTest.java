package com.aims.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
    }

    @Test
    void testUserId() {
        int expectedId = 1;
        user.setUserId(expectedId);
        assertEquals(expectedId, user.getUserId());
    }

    @Test
    void testUsername() {
        String expectedUsername = "testuser";
        user.setUsername(expectedUsername);
        assertEquals(expectedUsername, user.getUsername());
    }

    @Test
    void testPassword() {
        String expectedPassword = "password123";
        user.setPassword(expectedPassword);
        assertEquals(expectedPassword, user.getPassword());
    }

    @Test
    void testEmail() {
        String expectedEmail = "test@example.com";
        user.setEmail(expectedEmail);
        assertEquals(expectedEmail, user.getEmail());
    }

    @Test
    void testRole() {
        String expectedRole = "CUSTOMER";
        user.setRole(expectedRole);
        assertEquals(expectedRole, user.getRole());
    }

    @Test
    void testBlocked() {
        user.setBlocked(true);
        assertTrue(user.isBlocked());

        user.setBlocked(false);
        assertFalse(user.isBlocked());
    }

    @Test
    void testCreatedAt() {
        LocalDateTime expectedDateTime = LocalDateTime.now();
        user.setCreatedAt(expectedDateTime);
        assertEquals(expectedDateTime, user.getCreatedAt());
    }

    @Test
    void testCompleteUserSetup() {
        user.setUserId(1);
        user.setUsername("admin");
        user.setPassword("admin123");
        user.setEmail("admin@aims.com");
        user.setRole("ADMIN");
        user.setBlocked(false);
        user.setCreatedAt(LocalDateTime.now());

        assertEquals(1, user.getUserId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin123", user.getPassword());
        assertEquals("admin@aims.com", user.getEmail());
        assertEquals("ADMIN", user.getRole());
        assertFalse(user.isBlocked());
        assertNotNull(user.getCreatedAt());
    }
} 