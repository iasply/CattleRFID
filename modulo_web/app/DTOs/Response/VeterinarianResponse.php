<?php

namespace App\DTOs\Response;

use App\Models\User;

/**
 * Exposes only safe fields for a veterinarian.
 * Never leaks: password, tag_hash, remember_token, email_verified_at.
 */
readonly class VeterinarianResponse
{
    public function __construct(
        public int    $id,
        public string $name,
        public string $email,
        public string $vet_rfid,
        public bool   $is_veterinarian,
    )
    {
    }

    public static function fromModel(User $user): self
    {
        return new self(
            id: $user->id,
            name: $user->name,
            email: $user->email,
            vet_rfid: $user->vet_rfid,
            is_veterinarian: (bool)$user->is_veterinarian,
        );
    }

    public function toArray(): array
    {
        return [
            'id' => $this->id,
            'name' => $this->name,
            'email' => $this->email,
            'vet_rfid' => $this->vet_rfid,
            'is_veterinarian' => $this->is_veterinarian,
        ];
    }
}
