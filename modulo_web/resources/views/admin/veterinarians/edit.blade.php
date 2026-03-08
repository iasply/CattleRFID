@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.veterinarians.index') }}" style="color: var(--secondary); text-decoration: none;">← Voltar
            para Lista</a>
        <h2 style="margin-top: 1rem;">Editar Veterinário: {{ $veterinarian->name }}</h2>
    </div>

    <div class="card" style="max-width: 600px;">
        <form action="{{ route('admin.veterinarians.update', $veterinarian->id) }}" method="POST">
            @csrf
            @method('PUT')
            <div>
                <label>Nome Completo</label>
                <input type="text" name="name" value="{{ old('name', $veterinarian->name) }}" required>
            </div>

            <div>
                <label>Username</label>
                <input type="text" value="{{ $veterinarian->vet_rfid }}" readonly style="background-color: #f1f5f9;">
            </div>

            <div>
                <label>Email</label>
                <input type="email" name="email" value="{{ old('email', $veterinarian->email) }}" required>
                @error('email') <span class="error">{{ $message }}</span> @enderror
            </div>

            <div>
                <label>Nova Senha (deixe em branco para não alterar)</label>
                <input type="password" name="password">
            </div>

            <button type="submit" class="btn btn-primary" style="margin-top: 1rem; width: 100%;">Salvar Alterações</button>
        </form>
    </div>
@endsection