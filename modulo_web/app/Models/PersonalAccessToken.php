<?php

namespace App\Models;

use Laravel\Sanctum\PersonalAccessToken as SanctumPersonalAccessToken;

class PersonalAccessToken extends SanctumPersonalAccessToken
{
    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = [
        'name',
        'token',
        'abilities',
        'expires_at',
        'workstation_id',
    ];

    /**
     * Get the workstation that owns the token.
     */
    public function workstation()
    {
        return $this->belongsTo(Workstation::class);
    }
}
