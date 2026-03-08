@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.workstations.index') }}"
            style="text-decoration: none; color: var(--primary); font-weight: 500;">&larr; Voltar para Lista</a>
        <h2 style="margin-top: 1rem;">Nova Estação de Trabalho</h2>
    </div>

    <div class="card" style="max-width: 600px;">
        <form action="{{ route('admin.workstations.store') }}" method="POST">
            @csrf
            <div style="margin-bottom: 1.5rem;">
                <label for="desc" style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Descrição /
                    Localização</label>
                <input type="text" name="desc" id="desc" value="{{ old('desc') }}" required
                    style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 0.375rem;"
                    placeholder="Ex: Computador da Recepção">
                @error('desc')
                    <span
                        style="color: #dc2626; font-size: 0.875rem; margin-top: 0.25rem; display: block;">{{ $message }}</span>
                @enderror
            </div>

            <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                <button type="submit" class="btn btn-primary">Cadastrar Estação</button>
                <a href="{{ route('admin.workstations.index') }}" class="btn btn-secondary"
                    style="text-decoration: none; display: flex; align-items: center;">Cancelar</a>
            </div>
        </form>
    </div>
@endsection