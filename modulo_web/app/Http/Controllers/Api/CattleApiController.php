<?php

namespace App\Http\Controllers\Api;

use App\DTOs\Request\Cattle\StoreCattleRequest;
use App\DTOs\Response\CattleResponse;
use App\Http\Controllers\Controller;
use App\Models\Cattle;
use Illuminate\Http\JsonResponse;

class CattleApiController extends Controller
{
    public function index(): JsonResponse
    {
        $items = Cattle::all()->map(fn(Cattle $c) => CattleResponse::fromModel($c)->toArray());

        return response()->json($items);
    }

    public function store(StoreCattleRequest $request): JsonResponse
    {
        $cattle = Cattle::create(array_merge(
            $request->validated(),
            ['user_id' => $request->user()?->id],
        ));

        return response()->json([
            'message' => 'Animal cadastrado via API!',
            'cattle' => CattleResponse::fromModel($cattle)->toArray(),
        ], 201);
    }
}
