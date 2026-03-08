@extends('layouts.app')

@section('content')
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Histórico de Vacinação</h2>
        <a href="{{ route('admin.vaccines.create') }}" class="btn btn-primary"
            style="background-color: orange; border-color: orange;">+ Registrar Vacina</a>
    </div>

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Data</th>
                    <th>Animal (Tag)</th>
                    <th>Vacina</th>
                    <th>Peso na Aplicação</th>
                    <th>Veterinário</th>
                </tr>
            </thead>
            <tbody>
                @foreach($vaccines as $v)
                    <tr>
                        <td>{{ \Carbon\Carbon::parse($v->vaccination_date)->format('d/m/Y') }}</td>
                        <td><code>{{ $v->rfid_tag }}</code></td>
                        <td>{{ $v->vaccine_type }}</td>
                        <td>{{ number_format($v->current_weight, 2, ',', '.') }} kg</td>
                        <td>{{ $v->user->name ?? 'Sistema' }}</td>
                    </tr>
                @endforeach
                @if($vaccines->isEmpty())
                    <tr>
                        <td colspan="5" style="text-align: center; color: var(--secondary);">Nenhum registro de vacinação.</td>
                    </tr>
                @endif
            </tbody>
        </table>
    </div>
@endsection