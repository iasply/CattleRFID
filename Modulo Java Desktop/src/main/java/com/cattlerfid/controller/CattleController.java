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

    // 2. Inicia um pedido para gravar dados diretamente no Chip da Tag (ex: Nome do
    // animal)
    public void requestWriteTag(String dataToWrite) {
        if (!serialService.isOpen()) {
            if (viewListener != null)
                viewListener.onRfidWriteError("Porta Serial não conectada.");
            return;
        }
        serialService.requestWrite(dataToWrite);
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
                    processTagRead(parts[2].trim());
                }
            } else if (parts[1].equals("ERR")) {
                String cmdError = parts[2];
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
            currentEditingCattle = new Cattle();
            currentEditingCattle.setRfidTag(rfidTag);
            if (viewListener != null)
                viewListener.onRfidReadSuccess(currentEditingCattle, true);
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
