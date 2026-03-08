<?php

namespace App\DTOs\Request\Cattle;

use Illuminate\Foundation\Http\FormRequest;

class UpdateCattleRequest extends FormRequest
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
        ];
    }
}
