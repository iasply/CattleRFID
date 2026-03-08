@extends('layouts.app')

@section('content')
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Veterinários Cadastrados</h2>
        <a href="{{ route('admin.veterinarians.create') }}" class="btn btn-primary">+ Novo Veterinário</a>
    </div>

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                @foreach($vets as $vet)
                    <tr>
                        <td>{{ $vet->name }}</td>
                        <td><code>{{ $vet->vet_rfid }}</code></td>
                        <td>{{ $vet->email }}</td>
                        <td style="display: flex; gap: 0.5rem;">
                            <a href="{{ route('admin.veterinarians.show', $vet) }}" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none; background-color: #6366f1; border-color: #6366f1;">Ver</a>
                            <a href="{{ route('admin.veterinarians.edit', $vet) }}" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none;">Editar</a>
                        </td>
                    </tr>
                @endforeach
                @if($vets->isEmpty())
                    <tr>
                        <td colspan="4" style="text-align: center; color: var(--secondary);">Nenhum veterinário cadastrado.</td>
                    </tr>
                @endif
            </tbody>
        </table>
    </div>
@endsection