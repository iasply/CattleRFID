<?php

namespace App\DTOs\Response;

use App\Models\Cattle;

readonly class CattleResponse
{
    public function __construct(
        public int $id,
        public string $rfid_tag,
        public string $name,
        public float $weight,
        public string $registration_date,
    ) {
    }

    public static function fromModel(Cattle $cattle): self
    {
        return new self(
            id: $cattle->id,
            rfid_tag: $cattle->rfid_tag,
            name: $cattle->name,
            weight: (float) $cattle->weight,
            registration_date: $cattle->registration_date,
        );
    }

    public function toArray(): array
    {
        return [
            'id' => $this->id,
            'rfid_tag' => $this->rfid_tag,
            'name' => $this->name,
            'weight' => $this->weight,
            'registration_date' => $this->registration_date,
        ];
    }
}
