<?php

namespace App\Http\Controllers\Admin;

use App\DTOs\Request\Veterinarian\StoreVeterinarianRequest;
use App\DTOs\Request\Veterinarian\UpdateVeterinarianRequest;
use App\DTOs\Response\VaccineResponse;
use App\DTOs\Response\VeterinarianResponse;
use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class VeterinarianController extends Controller
{
    public function index()
    {
        $vets = User::where('is_veterinarian', true)->get()
            ->map(fn(User $u) => VeterinarianResponse::fromModel($u));

        return view('admin.veterinarians.index', compact('vets'));
    }

    public function store(StoreVeterinarianRequest $request)
    {
        User::create(array_merge($request->validated(), [
            'password' => Hash::make($request->password),
            'is_veterinarian' => true,
        ]));

        return redirect()->route('admin.veterinarians.index')
            ->with('success', 'Veterinário cadastrado com sucesso!');
    }

    public function create()
    {
        return view('admin.veterinarians.create');
    }

    public function show(User $veterinarian)
    {
        $veterinarian->load('vaccinations.workstation', 'vaccinations.cattle');
        $dto = VeterinarianResponse::fromModel($veterinarian);
        $vaccinations = $veterinarian->vaccinations->map(fn($v) => VaccineResponse::fromModel($v));

        return view('admin.veterinarians.show', [
            'veterinarian' => $dto,
            'vaccinations' => $vaccinations,
        ]);
    }

    public function edit(User $veterinarian)
    {
        $dto = VeterinarianResponse::fromModel($veterinarian);

        return view('admin.veterinarians.edit', ['veterinarian' => $dto]);
    }

    public function update(UpdateVeterinarianRequest $request, User $veterinarian)
    {
        $data = $request->validated();

        if (!empty($data['password'])) {
            $data['password'] = Hash::make($data['password']);
        } else {
            unset($data['password']);
        }

        $veterinarian->update($data);

        return redirect()->route('admin.veterinarians.index')
            ->with('success', 'Veterinário atualizado!');
    }

    public function destroy(User $veterinarian)
    {
        $veterinarian->delete();

        return redirect()->route('admin.veterinarians.index')
            ->with('success', 'Veterinário removido.');
    }
}
