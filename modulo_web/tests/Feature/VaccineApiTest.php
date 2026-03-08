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

    #[\PHPUnit\Framework\Attributes\Test]
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

        $vetTag = \App\Support\RfidGenerator::generateVetTag();

        // 2. Login via Workstation to get a token bound to that workstation
        $user->update(['vet_rfid' => $vetTag]);

        $loginResponse = $this->postJson('/api/login', [
            'workstation' => 'WS-API-TEST',
            'tag' => $vetTag,
        ]);

        $loginResponse->assertStatus(200);
        $token = $loginResponse->json('access_token');

        $tag1 = \App\Support\RfidGenerator::generateCattleTag();

        // 3. Create Cattle
        $cattle = Cattle::create([
            'rfid_tag' => $tag1,
            'name' => 'Test Cow',
            'weight' => 500.00,
            'registration_date' => now(),
        ]);

        // 4. Register Vaccination via API
        $vaccinationData = [
            'rfid_tag' => $tag1,
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
            'rfid_tag' => $tag1,
            'vaccine_type' => 'Aftosa',
            'current_weight' => 510.50,
            'user_id' => $user->id,
            'workstation_id' => $workstation->id,
        ]);

        // Verify cattle weight was updated
        $this->assertEquals(510.50, $cattle->fresh()->weight);
    }

    #[\PHPUnit\Framework\Attributes\Test]
    public function vaccine_registered_without_workstation_token_has_null_workstation_id()
    {
        $user = User::factory()->create(['is_veterinarian' => true]);

        // Login normally (via email) - token won't have workstation_id
        $token = $user->createToken('normal-token')->plainTextToken;

        $tag2 = \App\Support\RfidGenerator::generateCattleTag();

        Cattle::create([
            'rfid_tag' => $tag2,
            'name' => 'Another Cow',
            'weight' => 400.00,
            'registration_date' => now(),
        ]);

        $response = $this->withHeaders([
            'Authorization' => 'Bearer ' . $token,
        ])->postJson('/api/vaccines', [
                    'rfid_tag' => $tag2,
                    'vaccine_type' => 'Brucelose',
                    'current_weight' => 410.00,
                    'vaccination_date' => now()->format('Y-m-d'),
                ]);

        $response->assertStatus(201);

        $this->assertDatabaseHas('vaccines', [
            'rfid_tag' => $tag2,
            'workstation_id' => null,
        ]);
    }

    #[\PHPUnit\Framework\Attributes\Test]
    public function user_can_filter_vaccines_by_rfid_tag()
    {
        $user = User::factory()->create();
        $token = $user->createToken('test')->plainTextToken;
        $tagA = \App\Support\RfidGenerator::generateCattleTag();
        $tagB = \App\Support\RfidGenerator::generateCattleTag();

        Cattle::create(['rfid_tag' => $tagA, 'name' => 'Cow A', 'weight' => 100, 'registration_date' => now()]);
        Cattle::create(['rfid_tag' => $tagB, 'name' => 'Cow B', 'weight' => 100, 'registration_date' => now()]);

        Vaccine::create(['rfid_tag' => $tagA, 'vaccine_type' => 'Vax1', 'current_weight' => 105, 'vaccination_date' => now()]);
        Vaccine::create(['rfid_tag' => $tagA, 'vaccine_type' => 'Vax2', 'current_weight' => 110, 'vaccination_date' => now()]);
        Vaccine::create(['rfid_tag' => $tagB, 'vaccine_type' => 'Vax3', 'current_weight' => 105, 'vaccination_date' => now()]);

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->getJson("/api/vaccines?rfid_tag={$tagA}");

        $response->assertStatus(200)
            ->assertJsonCount(2, 'data');

        foreach ($response->json('data') as $v) {
            $this->assertEquals($tagA, $v['rfid_tag']);
        }
    }
}
