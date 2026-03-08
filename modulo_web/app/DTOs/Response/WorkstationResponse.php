<?php

namespace App\DTOs\Response;

use App\Models\Workstation;

readonly class WorkstationResponse
{
    public function __construct(
        public int $id,
        public string $hash,
        public string $desc,
    ) {
    }

    public static function fromModel(Workstation $workstation): self
    {
        return new self(
            id: $workstation->id,
            hash: $workstation->hash,
            desc: $workstation->desc,
        );
    }

    public function toArray(): array
    {
        return [
            'id' => $this->id,
            'hash' => $this->hash,
            'desc' => $this->desc,
        ];
    }
}
