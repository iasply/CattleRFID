<?php

namespace App\Http\Controllers\Api;

use App\DTOs\Request\Vaccine\StoreVaccineRequest;
use App\DTOs\Response\VaccineResponse;
use App\Http\Controllers\Controller;
use App\Models\Cattle;
use App\Models\Vaccine;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

class VaccineApiController extends Controller
{
    /**
     * Store a new vaccination.
     */
    public function store(StoreVaccineRequest $request): JsonResponse
    {
        $user = $request->user();
        $token = $user->currentAccessToken();

        $vaccine = Vaccine::create(array_merge(
            $request->validated(),
            [
                'user_id' => $user->id,
                'workstation_id' => $token->workstation_id ?? null,
            ],
        ));

        // Atualiza o peso do animal
        Cattle::where('rfid_tag', $request->rfid_tag)
            ->update(['weight' => $request->current_weight]);

        $vaccine->load('user', 'workstation');

        return response()->json([
            'message' => 'Vacinação registrada via API!',
            'vaccine' => VaccineResponse::fromModel($vaccine)->toArray(),
        ], 201);
    }

    /**
     * List vaccinations (with filtering).
     */
    public function index(Request $request): JsonResponse
    {
        $query = Vaccine::with(['cattle', 'user', 'workstation']);

        if ($request->has('rfid_tag')) {
            $query->where('rfid_tag', $request->rfid_tag);
        }

        $vaccines = $query->latest()->get();

        $response = $vaccines->map(fn($v) => VaccineResponse::fromModel($v)->toArray());

        return response()->json($response);
    }
}
