<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cattle extends Model
{
    protected $fillable = ['rfid_tag', 'name', 'weight', 'registration_date', 'user_id'];

    protected static function booted()
    {
        static::creating(function ($cattle) {
            if (!$cattle->rfid_tag || $cattle->rfid_tag === 'C') {
                $cattle->rfid_tag = 'C' . str_pad(static::max('id') + 1, 6, '0', STR_PAD_LEFT);
            }
            if (!$cattle->registration_date) {
                $cattle->registration_date = now()->toDateString();
            }
        });
    }

    /**
     * Get the user who registered the animal.
     */
    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function vaccines()
    {
        return $this->hasMany(Vaccine::class, 'rfid_tag', 'rfid_tag');
    }
}
