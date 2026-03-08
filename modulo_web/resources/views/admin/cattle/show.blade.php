@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.cattle.index') }}" style="color: var(--secondary); text-decoration: none;">← Voltar para
            Lista</a>
        <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-top: 1rem;">
            <div>
                <h2 style="margin: 0;">Animal: {{ $cattle->name }}</h2>
                <code style="font-size: 1.1rem; color: var(--primary);">Tag: {{ $cattle->rfid_tag }}</code>
            </div>
            <div style="text-align: right; color: var(--secondary);">
                <div>Peso Atual: <strong>{{ number_format($cattle->weight, 2, ',', '.') }} kg</strong></div>
                <div>Cadastrado em: {{ \Carbon\Carbon::parse($cattle->registration_date)->format('d/m/Y') }}</div>
            </div>
        </div>
    </div>

    <div class="card">
        <h3 style="margin-bottom: 1.5rem;">Histórico de Vacinação deste Animal</h3>
        <table>
            <thead>
                <tr>
                    <th>Data</th>
                    <th>Vacina</th>
                    <th>Peso Registrado</th>
                    <th>Veterinário</th>
                </tr>
            </thead>
            <tbody>
                @foreach($cattle->vaccines as $v)
                    <tr>
                        <td>{{ \Carbon\Carbon::parse($v->vaccination_date)->format('d/m/Y') }}</td>
                        <td>{{ $v->vaccine_type }}</td>
                        <td>{{ number_format($v->current_weight, 2, ',', '.') }} kg</td>
                        <td>{{ $v->user->name ?? 'Sistema' }}</td>
                    </tr>
                @endforeach
                @if($cattle->vaccines->isEmpty())
                    <tr>
                        <td colspan="4" style="text-align: center; color: var(--secondary);">Nenhuma vacina registrada para este
                            animal.</td>
                    </tr>
                @endif
            </tbody>
        </table>
    </div>
@endsection