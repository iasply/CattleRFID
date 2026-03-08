<?php

namespace App\Support;

use Illuminate\Support\Str;

class RfidGenerator
{
    /**
     * Gera uma tag RFID padronizada.
     * Inicia com 'C' e possui caracteres aleatórios (ex: C8F9A2B3D4).
     * O tamanho total é 11 (< 16 obrigatórios pelo BD).
     */
    public static function generateCattleTag(): string
    {
        return 'C' . strtoupper(Str::random(10));
    }

    /**
     * Gera uma tag RFID padronizada para Veterinários.
     * Inicia com 'V' e possui caracteres aleatórios (ex: V8F9A2B3D4).
     * O tamanho total é 11 (< 16 obrigatórios pelo BD).
     */
    public static function generateVetTag(): string
    {
        return 'V' . strtoupper(Str::random(10));
    }
}
