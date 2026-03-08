<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use App\Models\Vaccine;
use App\Models\Cattle;
use App\Models\User;

class VaccineController extends Controller
{
    public function index()
    {
        $vaccines = Vaccine::latest()->get();
        return view('admin.vaccines.index', compact('vaccines'));
    }

    public function create()
    {
        $gattos = Cattle::all();
        $vets = User::where('is_veterinarian', true)->get();
        return view('admin.vaccines.create', compact('gattos', 'vets'));
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'rfid_tag' => 'required|exists:cattle,rfid_tag',
            'vaccine_type' => 'required|string|max:255',
            'current_weight' => 'required|numeric',
            'vaccination_date' => 'required|date',
        ]);

        $data['user_id'] = auth()->id();
        Vaccine::create($data);

        // Atualiza o peso do animal
        $cattle = Cattle::where('rfid_tag', $data['rfid_tag'])->first();
        if ($cattle) {
            $cattle->update(['weight' => $data['current_weight']]);
        }

        return redirect()->route('admin.vaccines.index')->with('success', 'Vacinação registrada!');
    }
}
