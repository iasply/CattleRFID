<?php

namespace App\DTOs\Request\Vaccine;

use Illuminate\Foundation\Http\FormRequest;

class StoreVaccineRequest extends FormRequest
{
    public function authorize(): bool
    {
        return $this->user() && $this->user()->is_veterinarian;
    }

    public function rules(): array
    {
        return [
            'rfid_tag' => 'required|exists:cattle,rfid_tag|max:16',
            'vaccine_type' => 'required|string|max:255',
            'current_weight' => 'required|numeric|min:0',
            'vaccination_date' => 'required|date',
        ];
    }
}
