<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Vaccine extends Model
{
    protected $fillable = [
        'rfid_tag',
        'vaccine_type',
        'current_weight',
        'vaccination_date',
        'user_id',
        'workstation_id',
    ];

    /**
     * Get the user who administered the vaccine.
     */
    public function user()
    {
        return $this->belongsTo(User::class);
    }

    /**
     * Get the workstation used for this vaccination.
     */
    public function workstation()
    {
        return $this->belongsTo(Workstation::class);
    }
}
