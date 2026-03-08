<?php

namespace App\DTOs\Request\Cattle;

use Illuminate\Foundation\Http\FormRequest;

class StoreCattleRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'name' => 'required|string|max:255',
            'weight' => 'required|numeric|min:0',
            'rfid_tag' => 'nullable|string|unique:cattle,rfid_tag',
        ];
    }
}
