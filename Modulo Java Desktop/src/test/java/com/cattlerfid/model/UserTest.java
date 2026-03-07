package com.cattlerfid.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreationAndGetters() {
        User user = new User("marcelo_vet", "Marcelo Veterinario");

        assertEquals("marcelo_vet", user.getUsername());
        assertEquals("Marcelo Veterinario", user.getFullName());
    }

    @Test
    void testUserSetters() {
        User user = new User("temp", "Temp");
        user.setUsername("joao_vet");
        user.setFullName("Joao Silva");

        assertEquals("joao_vet", user.getUsername());
        assertEquals("Joao Silva", user.getFullName());
    }
}
