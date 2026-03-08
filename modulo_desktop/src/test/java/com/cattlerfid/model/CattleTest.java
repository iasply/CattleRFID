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
}
