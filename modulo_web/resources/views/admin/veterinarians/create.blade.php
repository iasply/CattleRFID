@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.veterinarians.index') }}" style="color: var(--secondary); text-decoration: none;">← Voltar
            para Lista</a>
        <h2 style="margin-top: 1rem;">Cadastrar Novo Veterinário</h2>
    </div>

    <div class="card" style="max-width: 600px;">
        <form action="{{ route('admin.veterinarians.store') }}" method="POST">
            @csrf
            <div>
                <label>Nome Completo</label>
                <input type="text" name="name" value="{{ old('name') }}" required placeholder="Ex: Dr. João Silva">
            </div>

            <div>
                <label>Email</label>
                <input type="email" name="email" value="{{ old('email') }}" required placeholder="email@exemplo.com">
                @error('email') <span class="error">{{ $message }}</span> @enderror
            </div>

            <div>
                <label>Senha Provisória</label>
                <input type="password" name="password" required>
            </div>

            <button type="submit" class="btn btn-primary" style="margin-top: 1rem; width: 100%;">Finalizar Cadastro</button>
        </form>
    </div>
@endsection