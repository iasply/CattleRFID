package com.cattlerfid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CattleTest {

    @Test
    void testCattleCreationAndGetters() {
        String date = "2026-03-07";
        Cattle cattle = new Cattle("TAG123", "Boi Bandido", 450.5, date);

        assertEquals("TAG123", cattle.getRfidTag());
        assertEquals("Boi Bandido", cattle.getName());
        assertEquals(450.5, cattle.getWeight());
        assertEquals(date, cattle.getRegistrationDate());
    }

    @Test
    void testCattleSetters() {
        Cattle cattle = new Cattle();

        cattle.setRfidTag("NEWTAG");
        cattle.setName("Mimosa");
        cattle.setWeight(300.0);
        String newDate = "2026-01-01";
        cattle.setRegistrationDate(newDate);

        assertEquals("NEWTAG", cattle.getRfidTag());
        assertEquals("Mimosa", cattle.getName());
        assertEquals(300.0, cattle.getWeight());
        assertEquals(newDate, cattle.getRegistrationDate());
    }

    @Test
    void testCattleDeserializationWithVaccinesCount() {
        String json = "{\"rfid_tag\":\"TAG123\",\"name\":\"Boi Bandido\",\"weight\":450.5,\"registration_date\":\"2026-03-07\",\"vaccines_count\":5}";
        com.google.gson.Gson gson = new com.google.gson.Gson();
        Cattle cattle = gson.fromJson(json, Cattle.class);

        assertEquals("TAG123", cattle.getRfidTag());
        assertEquals(5, cattle.getVaccinesCount());
    }
}
