<?php

namespace App\Http\Controllers\Admin;

use App\DTOs\Request\Cattle\StoreCattleRequest;
use App\DTOs\Request\Cattle\UpdateCattleRequest;
use App\DTOs\Response\CattleResponse;
use App\DTOs\Response\VaccineResponse;
use App\Http\Controllers\Controller;
use App\Models\Cattle;

class CattleController extends Controller
{
    public function index()
    {
        $gattos = Cattle::with('user')->get()->map(fn(Cattle $c) => CattleResponse::fromModel($c));

        return view('admin.cattle.index', compact('gattos'));
    }

    public function create()
    {
        return view('admin.cattle.create');
    }

    public function store(StoreCattleRequest $request)
    {
        Cattle::create(array_merge(
            $request->validated(),
            ['user_id' => auth()->id()],
        ));

        return redirect()->route('admin.cattle.index')->with('success', 'Animal cadastrado!');
    }

    public function show(Cattle $cattle)
    {
        $cattle->load('vaccines.user', 'vaccines.workstation');
        $dto = CattleResponse::fromModel($cattle);
        $vaccines = $cattle->vaccines->map(fn($v) => VaccineResponse::fromModel($v));

        return view('admin.cattle.show', ['cattle' => $dto, 'vaccines' => $vaccines]);
    }

    public function edit(Cattle $cattle)
    {
        $dto = CattleResponse::fromModel($cattle);

        return view('admin.cattle.edit', ['cattle' => $dto]);
    }

    public function update(UpdateCattleRequest $request, Cattle $cattle)
    {
        $cattle->update($request->validated());

        return redirect()->route('admin.cattle.index')->with('success', 'Dados do animal atualizados!');
    }

    public function destroy(Cattle $cattle)
    {
        $cattle->delete();

        return redirect()->route('admin.cattle.index')->with('success', 'Registro removido.');
    }
}
