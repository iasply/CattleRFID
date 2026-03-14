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

    /**
     * Valida se uma tag RFID é válida para o sistema.
     * Regras:
     * - Começa com 'C' ou 'V'
     * - Tamanho entre 2 e 16 caracteres
     * - Apenas caracteres alfanuméricos
     */
    public static boolean isValid(String rfid) {
        if (rfid == null || rfid.isEmpty()) {
            return false;
        }

        if (rfid.length() < 2 || rfid.length() > 16) {
            return false;
        }

        char prefix = Character.toUpperCase(rfid.charAt(0));
        if (prefix != 'C' && prefix != 'V') {
            return false;
        }

        return rfid.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Verifica se a tag é de um Animal (Cattle).
     */
    public static boolean isCattleTag(String rfid) {
        return isValid(rfid) && Character.toUpperCase(rfid.charAt(0)) == 'C';
    }

    /**
     * Verifica se a tag é de um Veterinário (User).
     */
    public static boolean isVetTag(String rfid) {
        return isValid(rfid) && Character.toUpperCase(rfid.charAt(0)) == 'V';
    }
}
