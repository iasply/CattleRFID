<?php

namespace App\Http\Controllers\Api;

use App\DTOs\Request\Cattle\StoreCattleRequest;
use App\DTOs\Request\Cattle\UpdateCattleRequest;
use App\Http\Resources\CattleResource;
use App\Http\Controllers\Controller;
use App\Models\Cattle;
use Illuminate\Http\JsonResponse;

class CattleApiController extends Controller
{
    public function __construct(
        protected \App\Services\CattleService $cattleService
    ) {
    }

    public function index(): JsonResponse
    {
        $items = Cattle::paginate(50);
        return response()->json(CattleResource::collection($items)->response()->getData(true));
    }

    public function indexWithVaccines(): JsonResponse
    {
        $items = \App\Models\CattleWithVaccinesView::paginate(50);

        // We iterate through the paginated view items and map them 
        // using CattleResource, injecting vaccines_count 
        $mappedItems = $items->getCollection()->map(function ($c) {
            $cattleModel = Cattle::find($c->id);
            if ($cattleModel) {
                $cattleModel->setAttribute('vaccines_count', $c->vaccines_count);
                return new CattleResource($cattleModel);
            }
            return null;
        })->filter();

        $items->setCollection($mappedItems);

        return response()->json($items);
    }

    public function store(StoreCattleRequest $request): JsonResponse
    {
        $cattle = $this->cattleService->createCattle($request->validated(), $request->user()?->id);

        return response()->json([
            'message' => 'Animal cadastrado via API!',
            'cattle' => new CattleResource($cattle),
        ], 201);
    }

    /**
     * Display the specified cattle by its RFID tag.
     */
    public function show(string $rfid_tag): JsonResponse
    {
        $cattle = Cattle::where('rfid_tag', $rfid_tag)->first();

        if (!$cattle) {
            return response()->json(['message' => 'Animal não encontrado.'], 404);
        }

        return response()->json(new CattleResource($cattle));
    }

    public function update(UpdateCattleRequest $request, Cattle $cattle): JsonResponse
    {
        $cattle = $this->cattleService->updateCattle($cattle, $request->validated());

        return response()->json([
            'message' => 'Animal atualizado via API!',
            'cattle' => new CattleResource($cattle),
        ]);
    }
}
