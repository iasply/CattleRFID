@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.vaccines.index') }}" style="color: var(--secondary); text-decoration: none;">← Voltar para
            Histórico</a>
        <h2 style="margin-top: 1rem;">Registrar Nova Aplicação de Vacina</h2>
    </div>

    <div class="card" style="max-width: 600px;">
        <form action="{{ route('admin.vaccines.store') }}" method="POST">
            @csrf
            <div>
                <label>Selecionar Animal (Tag RFID)</label>
                <select name="rfid_tag" required
                    style="width: 100%; padding: 0.5rem; border-radius: 0.375rem; border: 1px solid #cbd5e1;">
                    <option value="">-- Selecione o Animal --</option>
                    @foreach($gattos as $animal)
                        <option value="{{ $animal->rfid_tag }}" {{ old('rfid_tag') == $animal->rfid_tag ? 'selected' : '' }}>
                            {{ $animal->name }} ({{ $animal->rfid_tag }})
                        </option>
                    @endforeach
                </select>
                @error('rfid_tag') <span class="error">{{ $message }}</span> @enderror
            </div>

            <div>
                <label>Tipo de Vacina</label>
                <input type="text" name="vaccine_type" value="{{ old('vaccine_type') }}" required
                    placeholder="Ex: Febre Aftosa">
            </div>

            <div>
                <label>Peso do Animal na Aplicação (kg)</label>
                <input type="number" step="0.01" name="current_weight" value="{{ old('current_weight') }}" required>
            </div>

            <div>
                <label>Data da Aplicação</label>
                <input type="date" name="vaccination_date" value="{{ old('vaccination_date', date('Y-m-d')) }}" required>
            </div>

            <div>
                <label>Veterinário Responsável</label>
                <select name="vaccinator_username" required
                    style="width: 100%; padding: 0.5rem; border-radius: 0.375rem; border: 1px solid #cbd5e1;">
                    <option value="">-- Selecione o Veterinário --</option>
                    @foreach($vets as $vet)
                        <option value="{{ $vet->username }}" {{ old('vaccinator_username') == $vet->username ? 'selected' : '' }}>
                            {{ $vet->name }} ({{ $vet->username }})
                        </option>
                    @endforeach
                </select>
                @error('vaccinator_username') <span class="error">{{ $message }}</span> @enderror
            </div>

            <button type="submit" class="btn btn-primary"
                style="margin-top: 1.5rem; width: 100%; background-color: orange; border-color: orange;">Registrar
                Aplicação</button>
        </form>
    </div>
@endsection