@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.workstations.index') }}"
            style="text-decoration: none; color: var(--primary); font-weight: 500;">&larr; Voltar para Lista</a>
        <h2 style="margin-top: 1rem;">Editar Estação de Trabalho</h2>
    </div>

    <div class="card" style="max-width: 600px;">
        <form action="{{ route('admin.workstations.update', $workstation) }}" method="POST">
            @csrf
            @method('PUT')
            <div style="margin-bottom: 1.5rem;">
                <label for="hash" style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Hash da Estação
                    (Imutável)</label>
                <input type="text" id="hash" value="{{ $workstation->hash }}" readonly
                    style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 0.375rem; background-color: #f3f4f6; cursor: not-allowed;">
            </div>

            <div style="margin-bottom: 1.5rem;">
                <label for="desc" style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Descrição /
                    Localização</label>
                <input type="text" name="desc" id="desc" value="{{ old('desc', $workstation->desc) }}" required
                    style="width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 0.375rem;"
                    placeholder="Ex: Computador da Recepção">
                @error('desc')
                    <span
                        style="color: #dc2626; font-size: 0.875rem; margin-top: 0.25rem; display: block;">{{ $message }}</span>
                @enderror
            </div>

            <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                <button type="submit" class="btn btn-primary">Salvar Alterações</button>
                <a href="{{ route('admin.workstations.index') }}" class="btn btn-secondary"
                    style="text-decoration: none; display: flex; align-items: center;">Cancelar</a>
            </div>
        </form>
    </div>
@endsection