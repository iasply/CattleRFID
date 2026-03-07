package com.cattlerfid.service;

import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.Vaccine;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CattleApiService {

    // Simula um banco de dados em memoria
    private final List<Cattle> cattleDatabase = new ArrayList<>();
    private final List<Vaccine> vaccineDatabase = new ArrayList<>();

    public CattleApiService() {
        // Popula com dados mockados para testes de leitura
        cattleDatabase.add(new Cattle("TAG_BOI_100", "Mimosa", 450.0, LocalDate.of(2025, 10, 1)));
        cattleDatabase.add(new Cattle("TAG_BOI_101", "Nelore 1", 520.5, LocalDate.of(2026, 1, 15)));

        vaccineDatabase.add(new Vaccine("1", "TAG_BOI_100", LocalDate.of(2025, 11, 1), "joao_vet", "Aftosa", 460.0));
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

    public boolean saveVaccine(Vaccine vaccine) {
        if (vaccine == null || vaccine.getRfidTag() == null || vaccine.getRfidTag().trim().isEmpty()) {
            return false;
        }

        if (vaccine.getId() == null) {
            vaccine.setId(String.valueOf(System.currentTimeMillis()));
        }

        return vaccineDatabase.add(vaccine);
    }

    public List<Vaccine> getVaccinesByCattle(String rfidTag) {
        return vaccineDatabase.stream()
                .filter(v -> v.getRfidTag().equals(rfidTag))
                .collect(Collectors.toList());
    }
}
