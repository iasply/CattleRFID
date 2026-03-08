@extends('layouts.app')

@section('content')
    <x-page-header title="Veterinários Cadastrados">
        <x-slot name="actions">
            <x-button onclick="window.location='{{ route('admin.veterinarians.create') }}'">
                + Novo Veterinário
            </x-button>
        </x-slot>
    </x-page-header>

    <x-card>
        <x-table :headers="['Nome', 'Username', 'Email', 'Ações']">
            @foreach($vets as $vet)
                <tr>
                    <td>{{ $vet->name }}</td>
                    <td><code>{{ $vet->vet_rfid }}</code></td>
                    <td>{{ $vet->email }}</td>
                    <td class="text-right" style="display: flex; gap: 0.5rem; justify-content: flex-end;">
                        <a href="{{ route('admin.veterinarians.show', $vet->id) }}" class="btn btn-primary"
                            style="font-size: 0.75rem; text-decoration: none; padding: 0.4rem 0.8rem;">Ver</a>
                        <a href="{{ route('admin.veterinarians.edit', $vet->id) }}" class="btn btn-primary"
                            style="font-size: 0.75rem; text-decoration: none; padding: 0.4rem 0.8rem;">Editar</a>
                    </td>
                </tr>
            @endforeach
            @if($vets->isEmpty())
                <tr>
                    <td colspan="4" style="text-align: center; color: var(--secondary);">Nenhum veterinário cadastrado.</td>
                </tr>
            @endif
        </x-table>
    </x-card>
@endsection