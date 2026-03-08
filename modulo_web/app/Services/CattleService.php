<?php

namespace App\Services;

use App\Models\Cattle;
use Illuminate\Support\Facades\DB;

class CattleService
{
    /**
     * Cadastra um novo animal, gerando RFID e data de registro se necessário,
     * e garante a persistência via transação de banco.
     */
    public function createCattle(array $data, ?int $userId): Cattle
    {
        return DB::transaction(function () use ($data, $userId) {

            $data['user_id'] = $userId;

            if (empty($data['rfid_tag']) || $data['rfid_tag'] === 'C') {
                $latestId = Cattle::max('id') ?? 0;
                $data['rfid_tag'] = 'C' . str_pad($latestId + 1, 6, '0', STR_PAD_LEFT);
            }

            if (empty($data['registration_date'])) {
                $data['registration_date'] = now()->toDateString();
            }

            return Cattle::create($data);
        });
    }

    /**
     * Atualiza um animal dentro de uma transação
     */
    public function updateCattle(Cattle $cattle, array $data): Cattle
    {
        return DB::transaction(function () use ($cattle, $data) {
            $cattle->update($data);
            return $cattle;
        });
    }
}
