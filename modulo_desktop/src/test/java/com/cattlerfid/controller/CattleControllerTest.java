package com.cattlerfid.controller;

import com.cattlerfid.model.Cattle;
import com.cattlerfid.service.CattleApiService;
import com.cattlerfid.service.SerialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CattleControllerTest {

    private CattleApiService apiServiceMock;
    private SerialService serialServiceMock;
    private CattleController controller;
    private CattleController.CattleViewListener viewListenerMock;

    @BeforeEach
    void setUp() {
        apiServiceMock = mock(CattleApiService.class);
        serialServiceMock = mock(SerialService.class);
        viewListenerMock = mock(CattleController.CattleViewListener.class);

        controller = new CattleController(apiServiceMock, serialServiceMock);
        when(serialServiceMock.isOpen()).thenReturn(true);
        controller.setViewListener(viewListenerMock);
    }

    @Test
    void testRequestReadTagNotConnected() {
        when(serialServiceMock.isOpen()).thenReturn(false);
        controller.requestReadTag();

        verify(viewListenerMock).onRfidReadError(anyString());
        verify(serialServiceMock, never()).requestRead();
    }

    @Test
    void testRequestReadTagConnected() {
        controller.requestReadTag();
        verify(serialServiceMock).requestRead();
    }

    @Test
    void testRequestWriteTagNotConnected() {
        when(serialServiceMock.isOpen()).thenReturn(false);
        controller.requestWriteTag("C1234567890");

        verify(viewListenerMock).onRfidWriteError(anyString());
        verify(serialServiceMock, never()).requestWrite(anyString());
    }

    @Test
    void testRequestWriteTagConnected() {
        controller.requestWriteTag("C1234567890");
        verify(serialServiceMock).requestRead(); // Agora ele lê primeiro antes de gravar
    }

    @Test
    void testHandleMessageWriteReadUserTagBlocked() {
        // Simula o pedido pre-gravação
        controller.requestWriteTag("C123456");
        // Arduino devolve que leu uma TAG de usuario
        controller.handleIncomingSerialMessage("RES:OK:V_ADMIN_01:FW:92");

        verify(viewListenerMock).onRfidWriteError(contains("Bloqueado"));
        verify(serialServiceMock, never()).requestWrite(anyString());
    }

    @Test
    void testHandleMessageWriteReadNewTagAllowed() {
        // Simula o pedido pre-gravação
        controller.requestWriteTag("C123456");
        // Arduino devolve a TAG que lá estava (vazia, de animal, ou desconhecida)
        controller.handleIncomingSerialMessage("RES:OK:C_OLD_TAG:FW:92");

        // O comando write real entra em ação
        verify(serialServiceMock).requestWrite("C123456");
    }

    @Test
    void testHandleMessageWriteReadNoTagError() {
        // Simula o pedido pre-gravação
        controller.requestWriteTag("C123456");
        // Arduino tenta ler mas não acha tag
        controller.handleIncomingSerialMessage("RES:ERR:NO_TAG:FW:92");

        verify(viewListenerMock).onRfidWriteError(contains("Nenhuma Tag detectada"));
        verify(serialServiceMock, never()).requestWrite(anyString());
    }

    @Test
    void testHandleMessageReadSuccessExistingCattle() {
        String simulatedSerialMsg = "RES:OK:C       VACA_001:FW:92";
        Cattle existingCattle = new Cattle("C       VACA_001", "Mimosa", 400, LocalDate.now());

        when(apiServiceMock.getCattleByTag("C       VACA_001")).thenReturn(Optional.of(existingCattle));

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(apiServiceMock).getCattleByTag("C       VACA_001");
        verify(viewListenerMock).onRfidReadSuccess(existingCattle, false);
    }

    @Test
    void testHandleMessageReadSuccessNewCattle() {
        String simulatedSerialMsg = "RES:OK:C       DESCONHE:FW:92";

        when(apiServiceMock.getCattleByTag("C       DESCONHE")).thenReturn(Optional.empty());

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(apiServiceMock).getCattleByTag("C       DESCONHE");
        verify(viewListenerMock)
                .onRfidReadError("Animal não encontrado na base de dados. Por favor, cadastre-o primeiro.");

        Cattle c = controller.getCurrentEditingCattle();
        assertNull(c,
                "O animal não deve ser instanciado em modo de leitura se a tag for nova (agora bloqueado para o Vaccine Form)");
    }

    @Test
    void testHandleMessageReadUserTagWarning() {
        String simulatedSerialMsg = "RES:OK:V       VET_0001:FW:92";

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(viewListenerMock).onRfidReadError(contains("Tag de Usuário"));
    }

    @Test
    void testHandleMessageWriteSuccess() {
        String simulatedSerialMsg = "RES:OK:WROTE:FW:92";

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(viewListenerMock).onRfidWriteSuccess();
    }

    @Test
    void testHandleMessageWriteError() {
        String simulatedSerialMsg = "RES:ERR:WRITE_FAILED:FW:92";

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(viewListenerMock).onRfidWriteError("Erro no barramento SPI ao gravar dados na Tag.");
    }

    @Test
    void testSaveCattleSuccess() {
        Cattle newCattle = new Cattle();
        when(apiServiceMock.saveCattle(newCattle)).thenReturn(true);

        controller.saveCattleData(newCattle);

        verify(apiServiceMock).saveCattle(newCattle);
        verify(viewListenerMock).onApiSaveSuccess();
    }

    @Test
    void testSaveCattleError() {
        Cattle newCattle = new Cattle();
        when(apiServiceMock.saveCattle(newCattle)).thenReturn(false);

        controller.saveCattleData(newCattle);

        verify(viewListenerMock).onApiSaveError("Falha ao salvar animal na base de dados (Mock API).");
    }

    @Test
    void testSaveVaccineDataSuccess() {
        com.cattlerfid.model.Vaccine vaccine = new com.cattlerfid.model.Vaccine();
        Cattle cattle = new Cattle("C123", "Boi", 100.0, LocalDate.now());

        when(apiServiceMock.saveVaccine(vaccine)).thenReturn(true);
        when(apiServiceMock.saveCattle(cattle)).thenReturn(true);

        controller.saveVaccineData(vaccine, cattle, 150.0);

        assertEquals(150.0, cattle.getWeight());
        verify(apiServiceMock).saveVaccine(vaccine);
        verify(apiServiceMock).saveCattle(cattle);
        verify(viewListenerMock).onApiSaveSuccess();
    }

    @Test
    void testSaveVaccineDataError() {
        com.cattlerfid.model.Vaccine vaccine = new com.cattlerfid.model.Vaccine();
        Cattle cattle = new Cattle("C123", "Boi", 100.0, LocalDate.now());

        when(apiServiceMock.saveVaccine(vaccine)).thenReturn(false);
        when(apiServiceMock.saveCattle(cattle)).thenReturn(true);

        controller.saveVaccineData(vaccine, cattle, 150.0);

        verify(viewListenerMock).onApiSaveError("Falha ao registrar a vacina no banco de dados.");
    }

    @Test
    void testHandleMessageReadInvalidTagFormat() {
        String simulatedSerialMsg = "RES:OK:X       DESCONHE:FW:92";

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(viewListenerMock).onRfidReadError(contains("Formato de Tag animal inválido"));
    }

    @Test
    void testHandleMessageReadErrorNoTag() {
        String simulatedSerialMsg = "RES:ERR:NO_TAG:FW:92";

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(viewListenerMock).onRfidReadError("Nenhuma Tag detectada.");
    }

    @Test
    void testHandleMessageReadErrorAuth() {
        String simulatedSerialMsg = "RES:ERR:AUTH:FW:92";

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(viewListenerMock).onRfidReadError("Erro de autenticação da Tag.");
    }

    @Test
    void testHandleMessageReadErrorUnknown() {
        String simulatedSerialMsg = "RES:ERR:UNKNOWN_FAULT:FW:92";

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(viewListenerMock).onRfidReadError("Erro desconhecido: UNKNOWN_FAULT");
    }

    @Test
    void testGetters() {
        assertEquals(serialServiceMock, controller.getSerialService());
        assertEquals(apiServiceMock, controller.getApiService());
    }
}
