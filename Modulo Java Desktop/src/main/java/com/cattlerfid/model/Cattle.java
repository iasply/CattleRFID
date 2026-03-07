package com.cattlerfid.model;

import java.time.LocalDate;

public class Cattle {
    private String rfidTag;
    private String name;
    private double weight;
    private LocalDate lastVaccinationDate;
    private String vaccinatorUser; // Session user who applied the vaccine

    public Cattle(String rfidTag, String name, double weight, LocalDate lastVaccinationDate, String vaccinatorUser) {
        this.rfidTag = rfidTag;
        this.name = name;
        this.weight = weight;
        this.lastVaccinationDate = lastVaccinationDate;
        this.vaccinatorUser = vaccinatorUser;
    }

    public Cattle() {
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public void setRfidTag(String rfidTag) {
        this.rfidTag = rfidTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LocalDate getLastVaccinationDate() {
        return lastVaccinationDate;
    }

    public void setLastVaccinationDate(LocalDate lastVaccinationDate) {
        this.lastVaccinationDate = lastVaccinationDate;
    }

    public String getVaccinatorUser() {
        return vaccinatorUser;
    }

    public void setVaccinatorUser(String vaccinatorUser) {
        this.vaccinatorUser = vaccinatorUser;
    }
}
