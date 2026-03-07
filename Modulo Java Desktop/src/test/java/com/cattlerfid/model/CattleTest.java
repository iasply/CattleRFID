package com.cattlerfid.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CattleTest {

    @Test
    void testCattleCreationAndGetters() {
        LocalDate date = LocalDate.of(2026, 3, 7);
        Cattle cattle = new Cattle("TAG123", "Boi Bandido", 450.5, date, "marcelo_vet");

        assertEquals("TAG123", cattle.getRfidTag());
        assertEquals("Boi Bandido", cattle.getName());
        assertEquals(450.5, cattle.getWeight());
        assertEquals(date, cattle.getLastVaccinationDate());
        assertEquals("marcelo_vet", cattle.getVaccinatorUser());
    }

    @Test
    void testCattleSetters() {
        Cattle cattle = new Cattle();

        cattle.setRfidTag("NEWTAG");
        cattle.setName("Mimosa");
        cattle.setWeight(300.0);
        LocalDate newDate = LocalDate.now();
        cattle.setLastVaccinationDate(newDate);
        cattle.setVaccinatorUser("joao_vet");

        assertEquals("NEWTAG", cattle.getRfidTag());
        assertEquals("Mimosa", cattle.getName());
        assertEquals(300.0, cattle.getWeight());
        assertEquals(newDate, cattle.getLastVaccinationDate());
        assertEquals("joao_vet", cattle.getVaccinatorUser());
    }
}
