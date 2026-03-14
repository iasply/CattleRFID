@extends('layouts.app')

@section('content')
    <x-page-header title="Registrar Nova Aplicação de Vacina" :backLink="route('admin.vaccines.index')"
                   backText="Voltar para Histórico"/>

    <x-card maxWidth="600px">
        <form action="{{ route('admin.vaccines.store') }}" method="POST">
            @csrf

            <div style="margin-bottom: 1rem;">
                <label style="display: block; margin-bottom: 0.5rem;">Selecionar Animal (Tag RFID)</label>
                <select name="rfid_tag" required
                        style="width: 100%; padding: 0.5rem; border-radius: 0.375rem; border: 1px solid #cbd5e1;">
                    <option value="">-- Selecione o Animal --</option>
                    @foreach($gattos as $animal)
                        <option
                            value="{{ $animal->rfid_tag }}" {{ old('rfid_tag') == $animal->rfid_tag ? 'selected' : '' }}>
                            {{ $animal->name }} ({{ $animal->rfid_tag }})
                        </option>
                    @endforeach
                </select>
                @error('rfid_tag') <span
                    style="color: var(--danger); font-size: 0.875rem;">{{ $message }}</span> @enderror
            </div>

            <x-input label="Tipo de Vacina" name="vaccine_type" required placeholder="Ex: Febre Aftosa"/>

            <x-input label="Peso do Animal na Aplicação (kg)" name="current_weight" type="number" step="0.01" required/>

            <x-input label="Data da Aplicação" name="vaccination_date" type="date" :value="date('Y-m-d')" required/>

            <div style="margin-bottom: 1rem;">
                <label style="display: block; margin-bottom: 0.5rem;">Veterinário Responsável</label>
                <select name="vaccinator_username" required
                        style="width: 100%; padding: 0.5rem; border-radius: 0.375rem; border: 1px solid #cbd5e1;">
                    <option value="">-- Selecione o Veterinário --</option>
                    @foreach($vets as $vet)
                        <option
                            value="{{ $vet->username }}" {{ old('vaccinator_username') == $vet->username ? 'selected' : '' }}>
                            {{ $vet->name }} ({{ $vet->username }})
                        </option>
                    @endforeach
                </select>
                @error('vaccinator_username') <span
                    style="color: var(--danger); font-size: 0.875rem;">{{ $message }}</span>
                @enderror
            </div>

            <x-button type="submit" fullWidth
                      style="margin-top: 1.5rem; background-color: orange; border-color: orange;">
                Registrar Aplicação
            </x-button>
        </form>
    </x-card>
@endsection
