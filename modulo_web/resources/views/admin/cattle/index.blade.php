@extends('layouts.app')

@section('content')
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Rebanho Cadastrado</h2>
        <a href="{{ route('admin.cattle.create') }}" class="btn btn-success">+ Novo Animal</a>
    </div>

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Tag RFID</th>
                    <th>Nome/Apelido</th>
                    <th>Cadastrado por</th>
                    <th>Peso Atual</th>
                    <th>Data Registro</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                @foreach($gattos as $animal)
                    <tr>
                        <td><code>{{ $animal->rfid_tag }}</code></td>
                        <td>{{ $animal->name }}</td>
                        <td>{{ $animal->user_name ?? 'Sistema' }}</td>
                        <td>{{ number_format($animal->weight, 2, ',', '.') }} kg</td>
                        <td>{{ \Carbon\Carbon::parse($animal->registration_date)->format('d/m/Y') }}</td>
                        <td style="display: flex; gap: 0.5rem;">
                            <a href="{{ route('admin.cattle.show', $animal->id) }}" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none; background-color: #6366f1; border-color: #6366f1;">Ver</a>
                            <a href="{{ route('admin.cattle.edit', $animal->id) }}" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none;">Editar</a>
                        </td>
                    </tr>
                @endforeach
                @if($gattos->isEmpty())
                    <tr>
                        <td colspan="5" style="text-align: center; color: var(--secondary);">Nenhum animal cadastrado.</td>
                    </tr>
                @endif
            </tbody>
        </table>
    </div>
@endsection