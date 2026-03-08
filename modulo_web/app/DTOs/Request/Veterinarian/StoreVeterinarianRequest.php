<?php

namespace App\DTOs\Request\Veterinarian;

use Illuminate\Foundation\Http\FormRequest;

class StoreVeterinarianRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
            'vet_rfid' => 'nullable|string|max:16|unique:users,vet_rfid',
            'password' => 'required|min:6',
        ];
    }
}
