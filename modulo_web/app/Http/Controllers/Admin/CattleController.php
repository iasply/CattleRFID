<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use App\Models\Cattle;

class CattleController extends Controller
{
    public function index()
    {
        $gattos = Cattle::all();
        return view('admin.cattle.index', compact('gattos'));
    }

    public function create()
    {
        return view('admin.cattle.create');
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'name' => 'required|string|max:255',
            'weight' => 'required|numeric',
        ]);

        $data['user_id'] = auth()->id();
        Cattle::create($data);

        return redirect()->route('admin.cattle.index')->with('success', 'Animal cadastrado!');
    }

    public function show(Cattle $cattle)
    {
        $cattle->load('vaccines');
        return view('admin.cattle.show', compact('cattle'));
    }

    public function edit(Cattle $cattle)
    {
        return view('admin.cattle.edit', compact('cattle'));
    }

    public function update(Request $request, Cattle $cattle)
    {
        $data = $request->validate([
            'name' => 'required|string|max:255',
            'weight' => 'required|numeric',
        ]);

        $cattle->update($data);

        return redirect()->route('admin.cattle.index')->with('success', 'Dados do animal atualizados!');
    }

    public function destroy(Cattle $cattle)
    {
        $cattle->delete();
        return redirect()->route('admin.cattle.index')->with('success', 'Registro removido.');
    }
}
