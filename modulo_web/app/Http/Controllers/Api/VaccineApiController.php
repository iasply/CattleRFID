<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Vaccine;
use App\Models\Cattle;
use Illuminate\Http\Request;

class VaccineApiController extends Controller
{
    public function store(Request $request)
    {
        $user = $request->user();
        $token = $user->currentAccessToken();

        $data = $request->validate([
            'rfid_tag' => 'required|exists:cattle,rfid_tag',
            'vaccine_type' => 'required|string|max:255',
            'current_weight' => 'required|numeric',
            'vaccination_date' => 'required|date',
        ]);

        $vaccine = Vaccine::create(array_merge($data, [
            'user_id' => $user->id,
            'workstation_id' => $token->workstation_id ?? null,
        ]));

        // Atualiza o peso do animal
        $cattle = Cattle::where('rfid_tag', $data['rfid_tag'])->first();
        if ($cattle) {
            $cattle->update(['weight' => $data['current_weight']]);
        }

        return response()->json([
            'message' => 'Vacinação registrada via API!',
            'vaccine' => $vaccine
        ], 201);
    }
}
