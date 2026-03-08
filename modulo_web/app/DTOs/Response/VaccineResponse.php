<?php

namespace App\DTOs\Response;

use App\Models\Vaccine;

readonly class VaccineResponse
{
    public function __construct(
        public int $id,
        public string $rfid_tag,
        public string $vaccine_type,
        public float $current_weight,
        public string $vaccination_date,
        public ?string $veterinarian_name,
        public ?string $workstation_desc,
    ) {
    }

    public static function fromModel(Vaccine $vaccine): self
    {
        return new self(
            id: $vaccine->id,
            rfid_tag: $vaccine->rfid_tag,
            vaccine_type: $vaccine->vaccine_type,
            current_weight: (float) $vaccine->current_weight,
            vaccination_date: $vaccine->vaccination_date,
            veterinarian_name: $vaccine->relationLoaded('user') ? $vaccine->user?->name : null,
            workstation_desc: $vaccine->relationLoaded('workstation') ? $vaccine->workstation?->desc : null,
        );
    }

    public function toArray(): array
    {
        return [
            'id' => $this->id,
            'rfid_tag' => $this->rfid_tag,
            'vaccine_type' => $this->vaccine_type,
            'current_weight' => $this->current_weight,
            'vaccination_date' => $this->vaccination_date,
            'veterinarian_name' => $this->veterinarian_name,
            'workstation_desc' => $this->workstation_desc,
        ];
    }
}
