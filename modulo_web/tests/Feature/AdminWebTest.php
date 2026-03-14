<?php

namespace Tests\Feature;

use App\Models\Cattle;
use App\Models\User;
use App\Models\Vaccine;
use App\Support\RfidGenerator;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AdminWebTest extends TestCase
{
    use RefreshDatabase;

    public function test_components_load_correct_relation_data_in_admin_views()
    {
        $tag = RfidGenerator::generateCattleTag();
        $vetTag = RfidGenerator::generateVetTag();

        $admin = User::factory()->create();

        $vet = User::factory()->create([
            'name' => 'Dr. John Doe',
            'is_veterinarian' => true,
            'vet_rfid' => $vetTag,
        ]);

        $cattle = Cattle::create([
            'rfid_tag' => $tag,
            'name' => 'Boi Bandido',
            'weight' => 500,
            'registration_date' => now(),
            'user_id' => $admin->id,
        ]);

        $vaccine = Vaccine::create([
            'rfid_tag' => $tag,
            'vaccine_type' => 'Aftosa',
            'current_weight' => 550,
            'vaccination_date' => now(),
            'user_id' => $vet->id,
        ]);

        // Assert Animal Name shows on vaccines list view
        $response = $this->actingAs($admin)->get(route('admin.vaccines.index'));
        $response->assertStatus(200);
        $response->assertSee('Boi Bandido');
        $response->assertSee('Dr. John Doe'); // Assert Vet name shows too!

        // Assert Vet Name shows on cattle detail view instead of "Sistema"
        $response = $this->actingAs($admin)->get(route('admin.cattle.show', $cattle->id));
        $response->assertStatus(200);
        $response->assertSee('Dr. John Doe');

        // Assert Animal Name shows on vet detail view
        $response = $this->actingAs($admin)->get(route('admin.veterinarians.show', $vet->id));
        $response->assertStatus(200);
        $response->assertSee('Boi Bandido');
    }
}
