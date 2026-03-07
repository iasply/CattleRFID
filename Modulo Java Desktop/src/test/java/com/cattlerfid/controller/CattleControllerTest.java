package com.cattlerfid.controller;

import com.cattlerfid.model.Cattle;
import com.cattlerfid.service.CattleApiService;
import com.cattlerfid.service.SerialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void testHandleMessageReadSuccessExistingCattle() {
        String simulatedSerialMsg = "RES:OK:TAG_VACA:FW:92";
        Cattle existingCattle = new Cattle("TAG_VACA", "Mimosa", 400, LocalDate.now(), "vet");

        when(apiServiceMock.getCattleByTag("TAG_VACA")).thenReturn(Optional.of(existingCattle));

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(apiServiceMock).getCattleByTag("TAG_VACA");
        verify(viewListenerMock).onRfidReadSuccess(existingCattle, false);
    }

    @Test
    void testHandleMessageReadSuccessNewCattle() {
        String simulatedSerialMsg = "RES:OK:TAG_DESCONHECIDA:FW:92";

        when(apiServiceMock.getCattleByTag("TAG_DESCONHECIDA")).thenReturn(Optional.empty());

        controller.handleIncomingSerialMessage(simulatedSerialMsg);

        verify(apiServiceMock).getCattleByTag("TAG_DESCONHECIDA");
        verify(viewListenerMock).onRfidReadSuccess(any(Cattle.class), eq(true));

        Cattle c = controller.getCurrentEditingCattle();
        assertNotNull(c);
        assertEquals("TAG_DESCONHECIDA", c.getRfidTag());
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
}
