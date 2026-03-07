package com.cattlerfid.service;

import com.cattlerfid.model.Cattle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CattleApiServiceTest {

    private CattleApiService apiService;

    @BeforeEach
    void setUp() {
        apiService = new CattleApiService(); // Inicia com 2 dados mockados
    }

    @Test
    void testGetExistingCattle() {
        Optional<Cattle> cattleOpt = apiService.getCattleByTag("TAG_BOI_100");
        assertTrue(cattleOpt.isPresent());
        assertEquals("Mimosa", cattleOpt.get().getName());
    }

    @Test
    void testGetNonExistingCattle() {
        Optional<Cattle> cattleOpt = apiService.getCattleByTag("INEXISTENTE");
        assertFalse(cattleOpt.isPresent());
    }

    @Test
    void testSaveNewCattle() {
        Cattle newCattle = new Cattle("TAG_NOVO", "Bezerro", 100.0, LocalDate.now());
        boolean result = apiService.saveCattle(newCattle);
        assertTrue(result);

        assertEquals(3, apiService.getAllCattle().size());
        assertTrue(apiService.getCattleByTag("TAG_NOVO").isPresent());
    }

    @Test
    void testUpdateExistingCattle() {
        Cattle updatedCattle = new Cattle("TAG_BOI_100", "Mimosa Atualizada", 460.0, LocalDate.now());
        boolean result = apiService.saveCattle(updatedCattle);
        assertTrue(result);

        assertEquals(2, apiService.getAllCattle().size(), "O tamanho nao deve aumentar ao atualizar");
        Optional<Cattle> fetched = apiService.getCattleByTag("TAG_BOI_100");
        assertTrue(fetched.isPresent());
        assertEquals("Mimosa Atualizada", fetched.get().getName());
        assertEquals(460.0, fetched.get().getWeight());
    }

    @Test
    void testSaveInvalidCattle() {
        assertFalse(apiService.saveCattle(null));
        assertFalse(apiService.saveCattle(new Cattle(null, "Sem tag", 200, LocalDate.now())));
    }
}
