@extends('layouts.app')

@section('content')
    <div style="margin-bottom: 2rem;">
        <a href="{{ route('admin.veterinarians.index') }}" style="color: var(--secondary); text-decoration: none;">← Voltar
            para Lista</a>
        <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-top: 1rem;">
            <div>
                <h2 style="margin: 0;">Veterinário: {{ $veterinarian->name }}</h2>
                <code style="font-size: 1.1rem; color: var(--primary);">ID/Tag: {{ $veterinarian->vet_rfid }}</code>
            </div>
            <div style="text-align: right; color: var(--secondary);">
                <div>Email: {{ $veterinarian->email }}</div>
            </div>
        </div>
    </div>

    <div class="card">
        <h3 style="margin-bottom: 1.5rem;">Vacinas Aplicadas por este Veterinário</h3>
        <table>
            <thead>
                <tr>
                    <th>Data</th>
                    <th>Animal (Tag)</th>
                    <th>Vacina</th>
                    <th>Peso na Aplicação</th>
                </tr>
            </thead>
            <tbody>
                @foreach($vaccinations as $v)
                    <tr>
                        <td>{{ \Carbon\Carbon::parse($v->vaccination_date)->format('d/m/Y') }}</td>
                        <td><code>{{ $v->rfid_tag }}</code></td>
                        <td>{{ $v->vaccine_type }}</td>
                        <td>{{ number_format($v->current_weight, 2, ',', '.') }} kg</td>
                    </tr>
                @endforeach
                @if($vaccinations->isEmpty())
                    <tr>
                        <td colspan="4" style="text-align: center; color: var(--secondary);">Nenhuma vacina aplicada por este
                            profissional.</td>
                    </tr>
                @endif
            </tbody>
        </table>
    </div>
@endsection