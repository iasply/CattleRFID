<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class Workstation extends Model
{
    use HasFactory;

    protected $fillable = [
        'hash',
        'desc',
    ];

    /**
     * The "booted" method of the model.
     */
    protected static function booted()
    {
        static::creating(function ($workstation) {
            if (!$workstation->hash) {
                // Generates a unique short hash (8 chars) if none provided
                // Or a standard UUID for better uniqueness. Let's go with a clear pattern.
                $workstation->hash = 'WS-' . strtoupper(Str::random(8));
            }
        });

        static::updating(function ($workstation) {
            // Prevent changing the hash after creation
            if ($workstation->isDirty('hash')) {
                $workstation->hash = $workstation->getOriginal('hash');
            }
        });

        static::deleting(function ($workstation) {
            // Prevent deletion of workstations
            return false;
        });
    }
}
