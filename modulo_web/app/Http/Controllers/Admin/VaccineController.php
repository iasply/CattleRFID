<?php

namespace App\Http\Controllers\Admin;

use App\DTOs\Request\Vaccine\StoreVaccineRequest;
use App\DTOs\Response\VaccineResponse;
use App\Http\Controllers\Controller;
use App\Models\Cattle;
use App\Models\User;
use App\Models\Vaccine;

class VaccineController extends Controller
{
    public function index()
    {
        $vaccines = Vaccine::with('user', 'workstation', 'cattle')->latest()->get()
            ->map(fn(Vaccine $v) => VaccineResponse::fromModel($v));

        return view('admin.vaccines.index', compact('vaccines'));
    }

    public function create()
    {
        $gattos = Cattle::all();
        $vets = User::where('is_veterinarian', true)->get();

        return view('admin.vaccines.create', compact('gattos', 'vets'));
    }

    public function store(StoreVaccineRequest $request)
    {
        $vaccine = Vaccine::create(array_merge(
            $request->validated(),
            ['user_id' => auth()->id()],
        ));

        Cattle::where('rfid_tag', $request->rfid_tag)
            ->update(['weight' => $request->current_weight]);

        return redirect()->route('admin.vaccines.index')->with('success', 'Vacinação registrada!');
    }
}
