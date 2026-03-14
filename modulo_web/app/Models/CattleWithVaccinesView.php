<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CattleWithVaccinesView extends Model
{
    /**
     * Indicates if the model should be timestamped.
     *
     * @var bool
     */
    public $timestamps = false;
    /**
     * The table associated with the model.
     *
     * @var string
     */
    protected $table = 'cattle_with_vaccines_view';

    /**
     * Determine if the model is readonly.
     * @return bool
     */
    public function isReadOnly()
    {
        return true;
    }
}
