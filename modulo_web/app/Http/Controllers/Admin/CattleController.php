<?php

namespace App\Http\Controllers\Admin;

use App\DTOs\Request\Cattle\StoreCattleRequest;
use App\DTOs\Request\Cattle\UpdateCattleRequest;
use App\Http\Controllers\Controller;
use App\Models\Cattle;

class CattleController extends Controller
{
    public function __construct(
        protected \App\Services\CattleService $cattleService
    ) {
    }

    public function index()
    {
        $gattos = Cattle::with('user')->get();

        return view('admin.cattle.index', compact('gattos'));
    }

    public function create()
    {
        return view('admin.cattle.create');
    }

    public function store(StoreCattleRequest $request)
    {
        $this->cattleService->createCattle($request->validated(), auth()->id());

        return redirect()->route('admin.cattle.index')->with('success', 'Animal cadastrado!');
    }

    public function show(Cattle $cattle)
    {
        $cattle->load('vaccines.user', 'vaccines.workstation');

        return view('admin.cattle.show', ['cattle' => $cattle, 'vaccines' => $cattle->vaccines]);
    }

    public function edit(Cattle $cattle)
    {
        return view('admin.cattle.edit', ['cattle' => $cattle]);
    }

    public function update(UpdateCattleRequest $request, Cattle $cattle)
    {
        $this->cattleService->updateCattle($cattle, $request->validated());

        return redirect()->route('admin.cattle.index')->with('success', 'Dados do animal atualizados!');
    }

    public function destroy(Cattle $cattle)
    {
        $cattle->delete();

        return redirect()->route('admin.cattle.index')->with('success', 'Registro removido.');
    }
}
