<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Cattle;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class CattleUpdateApiTest extends TestCase
{
    use RefreshDatabase;

    #[\PHPUnit\Framework\Attributes\Test]
    public function user_can_update_existing_cattle_without_rfid_collision()
    {
        $user = User::factory()->create();
        $token = $user->createToken('test')->plainTextToken;

        $cattle = Cattle::create([
            'rfid_tag' => 'ANIMAL-UPDATE',
            'name' => 'Original Name',
            'weight' => 200.00,
            'registration_date' => '2024-01-01',
        ]);

        $updateData = [
            'name' => 'Updated Name',
            'weight' => 250.00,
        ];

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->putJson("/api/cattle/{$cattle->id}", $updateData);

        $response->assertStatus(200)
            ->assertJson([
                'cattle' => [
                    'id' => $cattle->id,
                    'name' => 'Updated Name',
                    'weight' => 250.00,
                    'rfid_tag' => 'ANIMAL-UPDATE', // RFID should remain same
                ]
            ]);

        $this->assertEquals('Updated Name', $cattle->fresh()->name);
        $this->assertEquals(250.00, $cattle->fresh()->weight);
    }

    #[\PHPUnit\Framework\Attributes\Test]
    public function updating_via_post_to_store_endpoint_fails_with_422_due_to_duplicate_rfid()
    {
        $user = User::factory()->create();
        $token = $user->createToken('test')->plainTextToken;

        Cattle::create([
            'rfid_tag' => 'EXISTING-TAG',
            'name' => 'Boi 1',
            'weight' => 100,
            'registration_date' => now(),
        ]);

        $duplicateData = [
            'rfid_tag' => 'EXISTING-TAG',
            'name' => 'Attempt Duplicate',
            'weight' => 200,
        ];

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->postJson('/api/cattle', $duplicateData);

        $response->assertStatus(422)
            ->assertJsonValidationErrors(['rfid_tag']);
    }
}
