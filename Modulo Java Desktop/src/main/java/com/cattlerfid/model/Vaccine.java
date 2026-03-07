package com.cattlerfid.model;

import java.time.LocalDate;

public class Vaccine {
    private String id;
    private String rfidTag; // Tag do Gado associado
    private LocalDate vaccinationDate;
    private String vaccinatorUser; // Usuário que aplicou a vacina
    private String vaccineType; // Tipo ou nome da Vacina (ex: Febre Aftosa)
    private double currentWeight; // Peso do animal no momento da vacinação

    public Vaccine(String id, String rfidTag, LocalDate vaccinationDate, String vaccinatorUser, String vaccineType,
                   double currentWeight) {
        this.id = id;
        this.rfidTag = rfidTag;
        this.vaccinationDate = vaccinationDate;
        this.vaccinatorUser = vaccinatorUser;
        this.vaccineType = vaccineType;
        this.currentWeight = currentWeight;
    }

    public Vaccine() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public void setRfidTag(String rfidTag) {
        this.rfidTag = rfidTag;
    }

    public LocalDate getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(LocalDate vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public String getVaccinatorUser() {
        return vaccinatorUser;
    }

    public void setVaccinatorUser(String vaccinatorUser) {
        this.vaccinatorUser = vaccinatorUser;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }
}
