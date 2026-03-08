<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

use App\Models\User;
use App\Models\Cattle;
use App\Models\Vaccine;

class DashboardController extends Controller
{
    public function index()
    {
        $stats = [
            'vets' => User::where('is_veterinarian', true)->count(),
            'cattle' => Cattle::count(),
            'vaccines' => Vaccine::count(),
        ];

        return view('admin.dashboard', compact('stats'));
    }
}
