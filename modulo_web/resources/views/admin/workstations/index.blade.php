@extends('layouts.app')

@section('content')
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Estações de Trabalho</h2>
        <a href="{{ route('admin.workstations.create') }}" class="btn btn-primary">+ Nova Estação</a>
    </div>

    @if(session('success'))
        <div class="alert alert-success"
            style="margin-bottom: 1rem; padding: 1rem; background-color: #d1fae5; color: #065f46; border-radius: 0.375rem;">
            {{ session('success') }}
        </div>
    @endif

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Hash</th>
                    <th>Descrição</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                @foreach($workstations as $ws)
                    <tr>
                        <td><code>{{ $ws->hash }}</code></td>
                        <td>{{ $ws->desc }}</td>
                        <td style="display: flex; gap: 0.5rem;">
                            <a href="{{ route('admin.workstations.edit', $ws) }}" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none;">Editar</a>
                        </td>
                    </tr>
                @endforeach
                @if($workstations->isEmpty())
                    <tr>
                        <td colspan="3" style="text-align: center; color: var(--secondary);">Nenhuma estação cadastrada.</td>
                    </tr>
                @endif
            </tbody>
        </table>
    </div>
@endsection