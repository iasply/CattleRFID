<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\Workstation;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    /**
     * Handle an incoming authentication request for API.
     */
    public function login(Request $request)
    {
        // Check for Workstation + Tag Hash login (Strictly for API/Desktop)
        if ($request->has(['workstation', 'tag'])) {
            $request->validate([
                'workstation' => 'required|string',
                'tag' => 'required|string',
            ]);

            // Validate Workstation
            $workstation = \App\Models\Workstation::where('hash', $request->workstation)->first();
            if (!$workstation) {
                throw ValidationException::withMessages([
                    'workstation' => ['Estação de trabalho não reconhecida.'],
                ]);
            }

            // Hash the incoming raw tag using global salt
            $hashedTag = hash('sha256', $request->tag . config('app.tag_salt'));

            // Validate Veterinarian Tag Hash
            $user = User::where('tag_hash', $hashedTag)
                ->where('is_veterinarian', true)
                ->first();

            if (!$user) {
                throw ValidationException::withMessages([
                    'tag' => ['Veterinário não encontrado ou tag inválida.'],
                ]);
            }

            // Create Token
            $tokenResult = $user->createToken('auth_token');

            // Associate workstation if provided
            if ($request->has('workstation')) {
                $workstation = Workstation::where('hash', $request->workstation)->first();
                if ($workstation) {
                    $tokenResult->accessToken->forceFill([
                        'workstation_id' => $workstation->id
                    ])->save();
                }
            }

            return response()->json([
                'access_token' => $tokenResult->plainTextToken,
                'token_type' => 'Bearer',
                'user' => $user,
                'workstation' => $workstation ?? null
            ]);
        }

        // Standard Identity/Password Login (Fallback for Admin/Mobile)
        $request->validate([
            'identity' => 'required', // Email or RFID Tag (username)
            'password' => 'required',
            'device_name' => 'required',
        ]);

        $user = User::where('email', $request->identity)
            ->orWhere('vet_rfid', $request->identity)
            ->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            throw ValidationException::withMessages([
                'identity' => ['As credenciais fornecidas estão incorretas.'],
            ]);
        }

        return response()->json([
            'access_token' => $user->createToken($request->device_name)->plainTextToken,
            'token_type' => 'Bearer',
            'user' => $user,
        ]);
    }

    /**
     * Revoke the current token.
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json(['message' => 'Token revogado com sucesso.']);
    }
}
