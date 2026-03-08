<?php

namespace App\DTOs\Request\Workstation;

use Illuminate\Foundation\Http\FormRequest;

class StoreWorkstationRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'desc' => 'required|string|max:255',
        ];
    }
}
