package com.cattlerfid.model;

import java.time.LocalDate;

public class Cattle {
    private String rfidTag;
    private String name;
    private double weight;
    private LocalDate registrationDate;

    public Cattle(String rfidTag, String name, double weight, LocalDate registrationDate) {
        this.rfidTag = rfidTag;
        this.name = name;
        this.weight = weight;
        this.registrationDate = registrationDate;
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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}
