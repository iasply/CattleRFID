<?php

namespace App\DTOs\Request\Veterinarian;

use Illuminate\Foundation\Http\FormRequest;

class UpdateVeterinarianRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        $veterinarianId = $this->route('veterinarian')?->id;

        return [
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email,' . $veterinarianId,
            'vet_rfid' => 'nullable|string|max:16|unique:users,vet_rfid,' . $veterinarianId,
            'password' => 'nullable|min:6',
        ];
    }
}
