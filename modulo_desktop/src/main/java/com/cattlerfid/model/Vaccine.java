package com.cattlerfid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Vaccine record.
 * Matches VaccineResponse DTO and StoreVaccineRequest fields.
 */
public class Vaccine {
    private String id;

    @SerializedName("rfid_tag")
    private String rfidTag;

    @SerializedName("vaccination_date")
    private String vaccinationDate; // Using String to simplify API parsing (YYYY-MM-DD)

    @SerializedName("vaccine_type")
    private String vaccineType;

    @SerializedName("current_weight")
    private double currentWeight;

    // Response-only fields
    @SerializedName("veterinarian_name")
    private String veterinarianName;

    @SerializedName("workstation_desc")
    private String workstationDesc;

    public Vaccine() {
    }

    public Vaccine(String id, String rfidTag, String vaccinationDate, String vaccineType, double currentWeight) {
        this.id = id;
        this.rfidTag = rfidTag;
        this.vaccinationDate = vaccinationDate;
        this.vaccineType = vaccineType;
        this.currentWeight = currentWeight;
    }

    // Getters and Setters

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

    public String getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(String vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
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

    public String getVeterinarianName() {
        return veterinarianName;
    }

    public void setVeterinarianName(String veterinarianName) {
        this.veterinarianName = veterinarianName;
    }

    public String getWorkstationDesc() {
        return workstationDesc;
    }

    public void setWorkstationDesc(String workstationDesc) {
        this.workstationDesc = workstationDesc;
    }
}
