<?php

namespace App\DTOs\Request\Auth;

use Illuminate\Foundation\Http\FormRequest;

/**
 * Standard login via email/vet_rfid + password (admin/mobile flow).
 */
class CredentialLoginRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'identity' => 'required|string',
            'password' => 'required|string',
            'device_name' => 'required|string',
        ];
    }
}
