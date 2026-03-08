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
    Route::get('/cattle', [CattleApiController::class, 'index']);
    Route::post('/cattle', [CattleApiController::class, 'store']);

    // Vaccine Endpoints
    Route::post('/vaccines', [VaccineApiController::class, 'store']);
});

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');
