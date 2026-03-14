<?php

namespace App\Http\Controllers\Api;

use App\DTOs\Request\Auth\TagLoginRequest;
use App\DTOs\Response\AuthResponse;
use App\DTOs\Response\VeterinarianResponse;
use App\DTOs\Response\WorkstationResponse;
use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\Workstation;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    /**
     * Route dispatcher: choose login .
     */
    public function login(Request $request): JsonResponse
    {
        return $this->loginWithTag(TagLoginRequest::createFrom($request));
    }

    /**
     * Handle workstation + tag login (IoT / Desktop flow).
     */
    public function loginWithTag(TagLoginRequest $request): JsonResponse
    {
        $workstation = Workstation::where('hash', $request->workstation)->first();

        if (!$workstation) {
            throw ValidationException::withMessages([
                'workstation' => ['Estação de trabalho não reconhecida.'],
            ]);
        }

        $hashedTag = hash('sha256', $request->tag . config('app.tag_salt'));

        $user = User::where('tag_hash', $hashedTag)
            ->where('is_veterinarian', true)
            ->first();

        if (!$user) {
            throw ValidationException::withMessages([
                'tag' => ['Veterinário não encontrado ou tag inválida.'],
            ]);
        }

        $tokenResult = $user->createToken('auth_token');

        $tokenResult->accessToken->forceFill([
            'workstation_id' => $workstation->id,
        ])->save();

        $response = new AuthResponse(
            access_token: $tokenResult->plainTextToken,
            token_type: 'Bearer',
            user: VeterinarianResponse::fromModel($user),
            workstation: WorkstationResponse::fromModel($workstation),
        );

        return response()->json($response->toArray());
    }

    /**
     * Revoke the current token.
     */
    public function logout(Request $request): JsonResponse
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json(['message' => 'Token revogado com sucesso.']);
    }
}
