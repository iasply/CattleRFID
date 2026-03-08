<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Cattle;
use App\Models\Workstation;
use App\Models\Vaccine;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class VaccineApiTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function vaccine_can_be_registered_via_api_and_associates_with_workstation()
    {
        // 1. Setup Workstation and User
        $workstation = Workstation::create([
            'hash' => 'WS-API-TEST',
            'desc' => 'API Test Station',
        ]);

        $user = User::factory()->create([
            'is_veterinarian' => true,
        ]);

        // 2. Login via Workstation to get a token bound to that workstation
        $user->update(['vet_rfid' => '999888777']);

        $loginResponse = $this->postJson('/api/login', [
            'workstation' => 'WS-API-TEST',
            'tag' => '999888777',
        ]);

        $loginResponse->assertStatus(200);
        $token = $loginResponse->json('access_token');

        // 3. Create Cattle
        $cattle = Cattle::create([
            'rfid_tag' => 'ANIMAL-001',
            'name' => 'Test Cow',
            'weight' => 500.00,
            'registration_date' => now(),
        ]);

        // 4. Register Vaccination via API
        $vaccinationData = [
            'rfid_tag' => 'ANIMAL-001',
            'vaccine_type' => 'Aftosa',
            'current_weight' => 510.50,
            'vaccination_date' => now()->format('Y-m-d'),
        ];

        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . $token,
        ])->postJson('/api/vaccines', $vaccinationData);

        // 5. Assertions
        $response->assertStatus(201);

        $this->assertDatabaseHas('vaccines', [
            'rfid_tag' => 'ANIMAL-001',
            'vaccine_type' => 'Aftosa',
            'current_weight' => 510.50,
            'user_id' => $user->id,
            'workstation_id' => $workstation->id,
        ]);

        // Verify cattle weight was updated
        $this->assertEquals(510.50, $cattle->fresh()->weight);
    }

    /** @test */
    public function vaccine_registered_without_workstation_token_has_null_workstation_id()
    {
        $user = User::factory()->create(['is_veterinarian' => true]);

        // Login normally (via email) - token won't have workstation_id
        $token = $user->createToken('normal-token')->plainTextToken;

        Cattle::create([
            'rfid_tag' => 'ANIMAL-002',
            'name' => 'Another Cow',
            'weight' => 400.00,
            'registration_date' => now(),
        ]);

        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . $token,
        ])->postJson('/api/vaccines', [
                    'rfid_tag' => 'ANIMAL-002',
                    'vaccine_type' => 'Brucelose',
                    'current_weight' => 410.00,
                    'vaccination_date' => now()->format('Y-m-d'),
                ]);

        $response->assertStatus(201);

        $this->assertDatabaseHas('vaccines', [
            'rfid_tag' => 'ANIMAL-002',
            'workstation_id' => null,
        ]);
    }

    /** @test */
    public function user_can_filter_vaccines_by_rfid_tag()
    {
        $user = User::factory()->create();
        $token = $user->createToken('test')->plainTextToken;

        Cattle::create(['rfid_tag' => 'TAG-A', 'name' => 'Cow A', 'weight' => 100, 'registration_date' => now()]);
        Cattle::create(['rfid_tag' => 'TAG-B', 'name' => 'Cow B', 'weight' => 100, 'registration_date' => now()]);

        Vaccine::create(['rfid_tag' => 'TAG-A', 'vaccine_type' => 'Vax1', 'current_weight' => 105, 'vaccination_date' => now()]);
        Vaccine::create(['rfid_tag' => 'TAG-A', 'vaccine_type' => 'Vax2', 'current_weight' => 110, 'vaccination_date' => now()]);
        Vaccine::create(['rfid_tag' => 'TAG-B', 'vaccine_type' => 'Vax3', 'current_weight' => 105, 'vaccination_date' => now()]);

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->getJson('/api/vaccines?rfid_tag=TAG-A');

        $response->assertStatus(200)
            ->assertJsonCount(2);

        foreach ($response->json() as $v) {
            $this->assertEquals('TAG-A', $v['rfid_tag']);
        }
    }
}
