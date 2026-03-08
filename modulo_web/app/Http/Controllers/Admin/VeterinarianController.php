<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use App\Models\User;
use Illuminate\Support\Facades\Hash;

class VeterinarianController extends Controller
{
    public function index()
    {
        $vets = User::where('is_veterinarian', true)->get();
        return view('admin.veterinarians.index', compact('vets'));
    }

    public function create()
    {
        return view('admin.veterinarians.create');
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|min:6',
        ]);

        $data['password'] = Hash::make($data['password']);
        $data['is_veterinarian'] = true;
        User::create($data);

        return redirect()->route('admin.veterinarians.index')->with('success', 'Veterinário cadastrado com sucesso!');
    }

    public function show(User $veterinarian)
    {
        $veterinarian->load('vaccinations');
        return view('admin.veterinarians.show', compact('veterinarian'));
    }

    public function edit(User $veterinarian)
    {
        return view('admin.veterinarians.edit', compact('veterinarian'));
    }

    public function update(Request $request, User $veterinarian)
    {
        $data = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email,' . $veterinarian->id,
            'password' => 'nullable|min:6',
        ]);

        if (!empty($data['password'])) {
            $data['password'] = Hash::make($data['password']);
        } else {
            unset($data['password']);
        }

        $veterinarian->update($data);

        return redirect()->route('admin.veterinarians.index')->with('success', 'Veterinário atualizado!');
    }

    public function destroy(User $veterinarian)
    {
        $veterinarian->delete();
        return redirect()->route('admin.veterinarians.index')->with('success', 'Veterinário removido.');
    }
}
