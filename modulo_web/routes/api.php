<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\CattleApiController;
use App\Http\Controllers\Api\VaccineApiController;

// Public API Routes
Route::post('/login', [AuthController::class, 'login']);

// Protected API Routes
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [AuthController::class, 'logout']);

    // Cattle Endpoints
    Route::apiResource('cattle', CattleApiController::class)->only(['index', 'store']);
    Route::get('cattle/{rfid_tag}', [CattleApiController::class, 'show']);
    Route::apiResource('vaccines', VaccineApiController::class)->only(['index', 'store']);
});

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');
