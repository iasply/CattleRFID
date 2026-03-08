package com.cattlerfid.util;

import java.util.UUID;

public class RfidGenerator {

    /**
     * Gera uma tag RFID padronizada para gado.
     * Inicia com 'C' e possui caracteres aleatórios (ex: C8F9A2B3D4).
     * O tamanho total é 11 (< 16 obrigatórios pelo BD).
     */
    public static String generateCattleTag() {
        return "C" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    /**
     * Gera uma tag RFID padronizada para Veterinários.
     * Inicia com 'V' e possui caracteres aleatórios.
     * O tamanho total é 11 (< 16 obrigatórios pelo BD).
     */
    public static String generateVetTag() {
        return "V" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
