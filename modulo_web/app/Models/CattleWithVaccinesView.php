<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CattleWithVaccinesView extends Model
{
    /**
     * The table associated with the model.
     *
     * @var string
     */
    protected $table = 'cattle_with_vaccines_view';

    /**
     * Indicates if the model should be timestamped.
     *
     * @var bool
     */
    public $timestamps = false;

    /**
     * Determine if the model is readonly.
     * @return bool
     */
    public function isReadOnly()
    {
        return true;
    }
}
