<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Cattle;
use Illuminate\Http\Request;

class CattleApiController extends Controller
{
    public function index()
    {
        return response()->json(Cattle::all());
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'name' => 'required|string|max:255',
            'weight' => 'required|numeric',
            'rfid_tag' => 'nullable|string|unique:cattle,rfid_tag',
        ]);

        $cattle = Cattle::create($data);

        return response()->json([
            'message' => 'Animal cadastrado via API!',
            'cattle' => $cattle
        ], 210);
    }
}
