package com.cattlerfid.controller;

import com.cattlerfid.model.Cattle;
import com.cattlerfid.service.CattleApiService;
import com.cattlerfid.service.SerialService;

import java.util.Optional;
import java.util.function.Consumer;

public class CattleController {

    private final CattleApiService apiService;
    private final SerialService serialService;

    // View Callbacks
    private CattleViewListener viewListener;
    private Cattle currentEditingCattle;
    private String pendingWriteData = null;
    private final Consumer<String> serialListener = this::handleIncomingSerialMessage;

    public interface CattleViewListener {
        void onRfidReadSuccess(Cattle cattle, boolean isNew);

        void onRfidReadError(String message);

        void onRfidWriteSuccess();

        void onRfidWriteError(String message);

        void onApiSaveSuccess();

        void onApiSaveError(String message);
    }

    public CattleController(CattleApiService apiService, SerialService serialService) {
        this.apiService = apiService;
        this.serialService = serialService;
    }

    public void setViewListener(CattleViewListener listener) {
        this.viewListener = listener;
        // Assegura que o parser de serial agora aponte pros listeners do Cattle
        if (serialService.isOpen()) {
            serialService.addMessageListener(serialListener);
        }
    }

    public void detachSerial() {
        serialService.removeMessageListener(serialListener);
    }

    // 1. Inicia um pedido para ler uma Tag
    public void requestReadTag() {
        if (!serialService.isOpen()) {
            if (viewListener != null)
                viewListener.onRfidReadError("Porta Serial não conectada.");
            return;
        }
        serialService.requestRead();
    }

    // 2. Inicia um pedido para verificar a tag pre-gravação
    public void requestWriteTag(String dataToWrite) {
        if (!serialService.isOpen()) {
            if (viewListener != null)
                viewListener.onRfidWriteError("Porta Serial não conectada.");
            return;
        }
        this.pendingWriteData = dataToWrite;
        serialService.requestRead(); // Valida fisicamente primeiro
    }

    // 3. Salva os dados completos do formulario (Mocked Database/API)
    public void saveCattleData(Cattle cattle) {
        boolean success = apiService.saveCattle(cattle);
        if (success) {
            if (viewListener != null)
                viewListener.onApiSaveSuccess();
        } else {
            if (viewListener != null)
                viewListener.onApiSaveError("Falha ao salvar animal na base de dados (Mock API).");
        }
    }

    // 4. Salva a vacina aplicada e atualiza o peso do animal
    public void saveVaccineData(com.cattlerfid.model.Vaccine vaccine, Cattle cattle, double currentWeight) {
        boolean vaccineSuccess = apiService.saveVaccine(vaccine);

        cattle.setWeight(currentWeight);
        boolean cattleSuccess = apiService.saveCattle(cattle);

        if (vaccineSuccess && cattleSuccess) {
            if (viewListener != null)
                viewListener.onApiSaveSuccess();
        } else {
            if (viewListener != null)
                viewListener.onApiSaveError("Falha ao registrar a vacina no banco de dados.");
        }
    }

    // Processa retorno do Arduino (Tanto respostas READ quanto respostas WRITE)
    protected void handleIncomingSerialMessage(String message) {
        // Ex read: RES:OK:TAG_BOI_100 :FW:92
        // Ex read error: RES:ERR:NO_TAG:FW:92
        // Ex write: RES:OK:WROTE:FW:92

        String[] parts = message.split(":");
        if (parts.length >= 2) {
            if (parts[1].equals("OK")) {
                if (parts.length > 2 && parts[2].equals("WROTE")) {
                    if (viewListener != null)
                        viewListener.onRfidWriteSuccess();
                } else if (parts.length > 2) {
                    String readTag = parts[2].trim();
                    if (pendingWriteData != null) {
                        // Modo pre-gravação
                        if (readTag.startsWith("V")) {
                            pendingWriteData = null; // aborta gravação
                            if (viewListener != null) {
                                viewListener.onRfidWriteError(
                                        "Bloqueado: Não é permitido sobrescrever uma Tag de Usuário.");
                            }
                        } else {
                            serialService.requestWrite(pendingWriteData);
                            pendingWriteData = null;
                        }
                    } else {
                        processTagRead(readTag);
                    }
                }
            } else if (parts[1].equals("ERR")) {
                String cmdError = parts[2];

                // Se der erro durante a leitura pre-gravação
                if (pendingWriteData != null) {
                    pendingWriteData = null;
                    if (viewListener != null) {
                        if (cmdError.equals("NO_TAG")) {
                            viewListener.onRfidWriteError("Nenhuma Tag detectada para gravação.");
                        } else {
                            viewListener.onRfidWriteError("Erro de leitura antes de gravar: " + cmdError);
                        }
                    }
                    return;
                }

                if (viewListener != null) {
                    if (cmdError.equals("WRITE_FAILED")) {
                        viewListener.onRfidWriteError("Erro no barramento SPI ao gravar dados na Tag.");
                    } else if (cmdError.equals("NO_TAG")) {
                        viewListener.onRfidReadError("Nenhuma Tag detectada.");
                    } else if (cmdError.equals("AUTH")) {
                        viewListener.onRfidReadError("Erro de autenticação da Tag.");
                    } else {
                        viewListener.onRfidReadError("Erro desconhecido: " + cmdError);
                    }
                }
            }
        }
    }

    private void processTagRead(String rfidTag) {
        if (!rfidTag.startsWith("C")) {
            if (rfidTag.startsWith("V")) {
                if (viewListener != null) {
                    viewListener.onRfidReadError(
                            "Atenção: Você leu uma Tag de Usuário (Veterinário) ao invés de um Animal.");
                }
            } else {
                if (viewListener != null) {
                    viewListener.onRfidReadError("Formato de Tag animal inválido (Requer prefixo C). Lido: " + rfidTag);
                }
            }
            return;
        }

        // Verifica se a Tag recemn-lida ja existe no banco principal
        Optional<Cattle> existing = apiService.getCattleByTag(rfidTag);

        if (existing.isPresent()) {
            currentEditingCattle = existing.get();
            if (viewListener != null)
                viewListener.onRfidReadSuccess(currentEditingCattle, false);
        } else {
            if (viewListener != null)
                viewListener.onRfidReadError("Animal não encontrado na base de dados. Por favor, cadastre-o primeiro.");
        }
    }

    public Cattle getCurrentEditingCattle() {
        return currentEditingCattle;
    }

    public SerialService getSerialService() {
        return serialService;
    }

    public CattleApiService getApiService() {
        return apiService;
    }
}
