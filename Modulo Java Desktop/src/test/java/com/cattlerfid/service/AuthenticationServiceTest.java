package com.cattlerfid.service;

import com.cattlerfid.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService();
    }

    @Test
    void testAuthenticateWithValidTag() {
        Optional<User> userOpt = authService.authenticateByTag("TAG_VET_01");
        assertTrue(userOpt.isPresent());
        assertEquals("joao_vet", userOpt.get().getUsername());
        assertEquals("Joao Silva", userOpt.get().getFullName());
    }

    @Test
    void testAuthenticateWithInvalidTag() {
        Optional<User> userOpt = authService.authenticateByTag("UNKNOWN_TAG");
        assertFalse(userOpt.isPresent());
    }

    @Test
    void testAuthenticateWithNullTag() {
        Optional<User> userOpt = authService.authenticateByTag(null);
        assertFalse(userOpt.isPresent());
    }
}
