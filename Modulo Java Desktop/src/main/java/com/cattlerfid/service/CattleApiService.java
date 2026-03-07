package com.cattlerfid.service;

import com.cattlerfid.model.Cattle;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CattleApiService {

    // Simula um banco de dados em memoria
    private final List<Cattle> cattleDatabase = new ArrayList<>();

    public CattleApiService() {
        // Popula com dados mockados para testes de leitura
        cattleDatabase.add(new Cattle("TAG_BOI_100", "Mimosa", 450.0, LocalDate.of(2025, 10, 1), "joao_vet"));
        cattleDatabase.add(new Cattle("TAG_BOI_101", "Nelore 1", 520.5, LocalDate.of(2026, 1, 15), "maria_vet"));
    }

    public Optional<Cattle> getCattleByTag(String rfidTag) {
        return cattleDatabase.stream()
                .filter(c -> c.getRfidTag().equals(rfidTag))
                .findFirst();
    }

    public boolean saveCattle(Cattle cattle) {
        if (cattle == null || cattle.getRfidTag() == null || cattle.getRfidTag().trim().isEmpty()) {
            return false;
        }

        // Se ja existe, atualiza
        Optional<Cattle> existing = getCattleByTag(cattle.getRfidTag());
        if (existing.isPresent()) {
            cattleDatabase.remove(existing.get());
        }

        return cattleDatabase.add(cattle);
    }

    public List<Cattle> getAllCattle() {
        return new ArrayList<>(cattleDatabase);
    }
}
