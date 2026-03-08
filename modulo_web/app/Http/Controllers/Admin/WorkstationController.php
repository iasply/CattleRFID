<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Workstation;
use Illuminate\Http\Request;

class WorkstationController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $workstations = Workstation::all();
        return view('admin.workstations.index', compact('workstations'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('admin.workstations.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $request->validate([
            'desc' => 'required|string',
        ]);

        Workstation::create($request->only('desc'));

        return redirect()->route('admin.workstations.index')
            ->with('success', 'Estação de trabalho cadastrada com sucesso.');
    }

    /**
     * Display the specified resource.
     */
    public function show(Workstation $workstation)
    {
        return view('admin.workstations.show', compact('workstation'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Workstation $workstation)
    {
        return view('admin.workstations.edit', compact('workstation'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Workstation $workstation)
    {
        $request->validate([
            'desc' => 'required|string',
        ]);

        $workstation->update($request->only('desc'));

        return redirect()->route('admin.workstations.index')
            ->with('success', 'Estação de trabalho atualizada com sucesso.');
    }
}
