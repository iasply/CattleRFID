@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.cattle.index') }}" style="color: var(--secondary); text-decoration: none;">← Voltar para
            Lista</a>
        <h2 style="margin-top: 1rem;">Editar Animal: {{ $cattle->rfid_tag }}</h2>
    </div>

    <div class="card" style="max-width: 600px;">
        <form action="{{ route('admin.cattle.update', $cattle->id) }}" method="POST">
            @csrf
            @method('PUT')
            <div>
                <label>Tag RFID</label>
                <input type="text" value="{{ $cattle->rfid_tag }}" readonly style="background-color: #f1f5f9;">
            </div>

            <div>
                <label>Nome / Apelido</label>
                <input type="text" name="name" value="{{ old('name', $cattle->name) }}" required>
            </div>

            <div>
                <label>Peso (kg)</label>
                <input type="number" step="0.01" name="weight" value="{{ old('weight', $cattle->weight) }}" required>
            </div>

            <button type="submit" class="btn btn-primary" style="margin-top: 1rem; width: 100%;">Salvar Alterações</button>
        </form>
    </div>
@endsection