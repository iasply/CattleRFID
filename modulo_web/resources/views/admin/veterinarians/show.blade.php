@extends('layouts.app')

@section('content')
    <x-page-header :title="'Veterinário: ' . $veterinarian->name" :backLink="route('admin.veterinarians.index')">
        <x-slot name="actions">
            <x-button variant="secondary"
                      onclick="window.location='{{ route('admin.veterinarians.edit', $veterinarian->id) }}'">
                Editar Perfil
            </x-button>
        </x-slot>
    </x-page-header>

    <div
        style="display: grid; grid-template-columns: 1fr; gap: 2rem; margin-bottom: 2rem; @media (min-width: 1024px) { grid-template-columns: 350px 1fr; }">
        <x-card>
            <div
                style="display: flex; flex-direction: column; align-items: center; padding-bottom: 1.5rem; border-bottom: 1px solid var(--bg-main); margin-bottom: 1.5rem;">
                <div
                    style="width: 80px; height: 80px; background: var(--bg-sidebar); color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2rem; margin-bottom: 1rem;">
                    👨‍⚕️
                </div>
                <h3 style="font-size: 1.25rem; font-weight: 800; text-align: center;">{{ $veterinarian->name }}</h3>
                <span
                    style="color: var(--primary-dark); font-weight: 600; font-size: 0.875rem;">Médico Veterinário</span>
            </div>

            <div style="display: flex; flex-direction: column; gap: 1.25rem;">
                <div>
                    <span
                        style="display: block; font-size: 0.75rem; text-transform: uppercase; color: var(--text-muted); font-weight: 700; margin-bottom: 0.25rem;">ID
                        / RFID</span>
                    <code
                        style="font-size: 1rem; color: var(--text-main); font-weight: 600;">{{ $veterinarian->vet_rfid }}</code>
                </div>

                <div>
                    <span
                        style="display: block; font-size: 0.75rem; text-transform: uppercase; color: var(--text-muted); font-weight: 700; margin-bottom: 0.25rem;">Email
                        de Contato</span>
                    <span style="font-size: 0.9375rem; font-weight: 500;">{{ $veterinarian->email }}</span>
                </div>

                <div
                    style="padding: 1rem; border-radius: var(--radius-md); background: #f0fdf4; border: 1px solid #dcfce7;">
                    <span
                        style="display: block; font-size: 0.75rem; text-transform: uppercase; color: #166534; font-weight: 700; margin-bottom: 0.25rem;">Status
                        na Plataforma</span>
                    <span style="color: #15803d; font-weight: 700; font-size: 0.875rem;">● Ativo no Sistema</span>
                </div>
            </div>
        </x-card>

        <x-card>
            <h3 style="margin-bottom: 1.5rem; font-size: 1.125rem; font-weight: 700;">Histórico de Aplicações</h3>

            <x-table :headers="['Data', 'Animal', 'Vacina Aplicada', 'Peso']">
                @foreach($vaccinations as $v)
                    <tr>
                        <td>{{ \Carbon\Carbon::parse($v->vaccination_date)->format('d/m/Y') }}</td>
                        <td>
                            <div style="display: flex; flex-direction: column;">
                                <span style="font-weight: 600;">{{ $v->animal_name ?? 'Desconhecido' }}</span>
                                <code style="font-size: 0.75rem;">{{ $v->rfid_tag }}</code>
                            </div>
                        </td>
                        <td>
                            <span
                                style="display: inline-block; padding: 0.25rem 0.5rem; background: var(--bg-main); border-radius: 4px; font-weight: 600; font-size: 0.8125rem;">
                                {{ $v->vaccine_type }}
                            </span>
                        </td>
                        <td class="text-right">{{ number_format($v->current_weight, 2, ',', '.') }} kg</td>
                    </tr>
                @endforeach
                @if($vaccinations->isEmpty())
                    <tr>
                        <td colspan="4" style="text-align: center; color: var(--secondary); padding: 2rem;">Nenhuma
                            vacina
                            aplicada por este profissional.
                        </td>
                    </tr>
                @endif
            </x-table>
        </x-card>
    </div>
@endsection
