<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class VaccineResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'rfid_tag' => $this->rfid_tag,
            'vaccine_type' => $this->vaccine_type,
            'current_weight' => (float)$this->current_weight,
            'vaccination_date' => $this->vaccination_date,
            'user_id' => $this->user_id,
            'workstation_id' => $this->workstation_id,
            'created_at' => $this->created_at,
        ];
    }
}
