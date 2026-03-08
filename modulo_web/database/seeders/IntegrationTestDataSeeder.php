<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Workstation;
use Illuminate\Support\Facades\Hash;

class IntegrationTestDataSeeder extends Seeder
{
    public function run(): void
    {
        User::updateOrCreate(
            ['vet_rfid' => 'V000002'],
            [
                'name' => 'Vet Integration Test',
                'email' => 'vet2@cattlerfid.com',
                'password' => Hash::make('password123'),
                'is_veterinarian' => true,
            ]
        );

        Workstation::updateOrCreate(
            ['hash' => 'WS-XTYBQRG6'],
            ['desc' => 'Workstation Integration Test']
        );

        User::where('vet_rfid', 'V000002')->first()->cattle()->updateOrCreate(
            ['rfid_tag' => 'C000002'],
            [
                'name' => 'Cattle Integration Test',
                'weight' => 200.0,
                'registration_date' => now()->format('Y-m-d'),
            ]
        );
    }
}
