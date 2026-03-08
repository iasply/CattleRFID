@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.cattle.index') }}" style="color: var(--secondary); text-decoration: none;">← Voltar para
            Lista</a>
        <h2 style="margin-top: 1rem;">Cadastrar Novo Animal</h2>
    </div>

    <div class="card" style="max-width: 600px;">
        <form action="{{ route('admin.cattle.store') }}" method="POST">
            @csrf
            <div>
                <label>Nome / Apelido</label>
                <input type="text" name="name" value="{{ old('name') }}" required placeholder="Ex: Mimosa">
            </div>

            <div>
                <label>Peso Inicial (kg)</label>
                <input type="number" step="0.01" name="weight" value="{{ old('weight') }}" required>
            </div>

            <button type="submit" class="btn btn-success" style="margin-top: 1rem; width: 100%;">Finalizar Cadastro</button>
        </form>
    </div>
@endsection