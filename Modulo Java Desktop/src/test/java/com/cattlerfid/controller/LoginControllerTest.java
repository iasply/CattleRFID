package com.cattlerfid.controller;

import com.cattlerfid.model.User;
import com.cattlerfid.service.AuthenticationService;
import com.cattlerfid.service.SerialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private AuthenticationService authServiceMock;
    private SerialService serialServiceMock;
    private LoginController controller;
    private LoginController.LoginViewListener viewListenerMock;

    @BeforeEach
    void setUp() {
        authServiceMock = mock(AuthenticationService.class);
        serialServiceMock = mock(SerialService.class);
        viewListenerMock = mock(LoginController.LoginViewListener.class);

        controller = new LoginController(authServiceMock, serialServiceMock);
        controller.setViewListener(viewListenerMock);
    }

    @Test
    void testStartConnectionSuccess() {
        when(serialServiceMock.connect("COM3")).thenReturn(true);

        controller.startSerialConnection("COM3");

        verify(serialServiceMock).connect("COM3");
        verify(serialServiceMock).setOnMessageReceived(any());
        verify(viewListenerMock).onSerialConnected();
    }

    @Test
    void testStartConnectionFailure() {
        when(serialServiceMock.connect("COM99")).thenReturn(false);

        controller.startSerialConnection("COM99");

        verify(viewListenerMock).onSerialError(anyString());
        verify(serialServiceMock, never()).setOnMessageReceived(any());
    }

    @Test
    void testHandleMessageSuccessfulReadValidUser() {
        // Simula Tag chegando pela Serial
        String simulatedArduinoResponse = "RES:OK:TAG_VET_01:FW:92";

        // Simula db mock
        User mockedVet = new User("joao_vet", "Joao");
        when(authServiceMock.authenticateByTag("TAG_VET_01")).thenReturn(Optional.of(mockedVet));

        controller.handleIncomingSerialMessage(simulatedArduinoResponse);

        verify(authServiceMock).authenticateByTag("TAG_VET_01");
        verify(viewListenerMock).onLoginSuccess(mockedVet);
        assertEquals(mockedVet, controller.getLoggedUser());
    }

    @Test
    void testHandleMessageSuccessfulReadInvalidUser() {
        String simulatedArduinoResponse = "RES:OK:UNKNOWN:FW:92";

        when(authServiceMock.authenticateByTag("UNKNOWN")).thenReturn(Optional.empty());

        controller.handleIncomingSerialMessage(simulatedArduinoResponse);

        verify(viewListenerMock).onLoginError("Acesso Negado: Tag não cadastrada como funcionário VET.");
        assertNull(controller.getLoggedUser());
    }

    @Test
    void testHandleMessageArduinoErrorNoTag() {
        String simulatedArduinoResponse = "RES:ERR:NO_TAG:FW:92";

        controller.handleIncomingSerialMessage(simulatedArduinoResponse);

        verify(viewListenerMock).onLoginError("Nenhuma Tag ou Crachá detectado a tempo.");
        verify(authServiceMock, never()).authenticateByTag(any());
    }
}
