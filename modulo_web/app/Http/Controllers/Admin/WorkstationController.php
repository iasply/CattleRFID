<?php

namespace App\Http\Controllers\Admin;

use App\DTOs\Request\Workstation\StoreWorkstationRequest;
use App\DTOs\Request\Workstation\UpdateWorkstationRequest;
use App\DTOs\Response\WorkstationResponse;
use App\Http\Controllers\Controller;
use App\Models\Workstation;

class WorkstationController extends Controller
{
    public function index()
    {
        $workstations = Workstation::all()
            ->map(fn(Workstation $w) => WorkstationResponse::fromModel($w));

        return view('admin.workstations.index', compact('workstations'));
    }

    public function create()
    {
        return view('admin.workstations.create');
    }

    public function store(StoreWorkstationRequest $request)
    {
        Workstation::create($request->validated());

        return redirect()->route('admin.workstations.index')
            ->with('success', 'Estação de trabalho cadastrada com sucesso.');
    }

    public function show(Workstation $workstation)
    {
        $dto = WorkstationResponse::fromModel($workstation);

        return view('admin.workstations.show', ['workstation' => $dto]);
    }

    public function edit(Workstation $workstation)
    {
        $dto = WorkstationResponse::fromModel($workstation);

        return view('admin.workstations.edit', ['workstation' => $dto]);
    }

    public function update(UpdateWorkstationRequest $request, Workstation $workstation)
    {
        $workstation->update($request->validated());

        return redirect()->route('admin.workstations.index')
            ->with('success', 'Estação de trabalho atualizada com sucesso.');
    }
}
