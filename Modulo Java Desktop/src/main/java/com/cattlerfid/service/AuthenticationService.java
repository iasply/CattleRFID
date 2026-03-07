package com.cattlerfid.service;

import com.cattlerfid.model.User;
import java.util.Optional;

public class AuthenticationService {

    // Simula um banco de dados de usuarios validos por Tag RFID
    public Optional<User> authenticateByTag(String rfidTag) {
        if (rfidTag == null || rfidTag.trim().isEmpty()) {
            return Optional.empty();
        }

        // Mock de usuarios
        if (rfidTag.equals("TAG_VET_01")) {
            return Optional.of(new User("joao_vet", "Joao Silva"));
        } else if (rfidTag.equals("TAG_VET_02")) {
            return Optional.of(new User("maria_vet", "Maria Souza"));
        }

        return Optional.empty(); // Tag nao reconhecida como usuario
    }
}
