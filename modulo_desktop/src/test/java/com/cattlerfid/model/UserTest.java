package com.cattlerfid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void testUserCreationAndGetters() {
        User user = new User("marcelo_vet", "Marcelo Veterinario");

        assertEquals("marcelo_vet", user.getVetRfid());
        assertEquals("Marcelo Veterinario", user.getName());
    }

    @Test
    void testUserSetters() {
        User user = new User("temp", "Temp");
        user.setVetRfid("joao_vet");
        user.setName("Joao Silva");

        assertEquals("joao_vet", user.getVetRfid());
        assertEquals("Joao Silva", user.getName());
    }
}
