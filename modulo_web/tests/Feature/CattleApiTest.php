<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Cattle;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class CattleApiTest extends TestCase
{
    use RefreshDatabase;

    #[\PHPUnit\Framework\Attributes\Test]
    public function user_can_lookup_cattle_by_rfid_tag()
    {
        $user = User::factory()->create();
        $token = $user->createToken('test')->plainTextToken;

        Cattle::create([
            'rfid_tag' => 'TEST-TAG-123',
            'name' => 'Boi Teste',
            'weight' => 450.50,
            'registration_date' => '2023-01-01',
        ]);

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->getJson('/api/cattle/TEST-TAG-123');

        $response->assertStatus(200)
            ->assertJson([
                'rfid_tag' => 'TEST-TAG-123',
                'name' => 'Boi Teste',
                'weight' => 450.50,
            ]);
    }

    #[\PHPUnit\Framework\Attributes\Test]
    public function lookup_returns_404_for_non_existent_tag()
    {
        $user = User::factory()->create();
        $token = $user->createToken('test')->plainTextToken;

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->getJson('/api/cattle/NON-EXISTENT');

        $response->assertStatus(404)
            ->assertJson(['message' => 'Animal não encontrado.']);
    }

    #[\PHPUnit\Framework\Attributes\Test]
    public function user_can_list_all_cattle()
    {
        $user = User::factory()->create();
        $token = $user->createToken('test')->plainTextToken;

        Cattle::create(['rfid_tag' => 'TAG-1', 'name' => 'A', 'weight' => 100, 'registration_date' => now()]);
        Cattle::create(['rfid_tag' => 'TAG-2', 'name' => 'B', 'weight' => 200, 'registration_date' => now()]);

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->getJson('/api/cattle');

        $response->assertStatus(200)
            ->assertJsonCount(2);
    }
}
