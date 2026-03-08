package com.cattlerfid.service;

import com.cattlerfid.config.ApiConfig;
import com.cattlerfid.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private AuthenticationService authService;

    @Mock
    private ApiConfig config;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(config, httpClient);
    }

    @Test
    void testAuthenticateByTagSuccess() throws IOException, InterruptedException {
        // Arrange
        String rawTag = "VET_TAG_123";
        String workstationHash = "WS_HASH_XYZ";
        String jsonResponse = "{\"access_token\":\"secret_token\",\"user\":{\"vet_rfid\":\"joao_vet\",\"name\":\"Joao Silva\"}}";

        when(config.getWorkstationHash()).thenReturn(workstationHash);
        when(config.url("/login")).thenReturn("http://localhost/api/login");
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        Optional<User> result = authService.authenticateByTag(rawTag);

        // Assert
        assertTrue(result.isPresent());
        User user = result.get();
        assertEquals("joao_vet", user.getUsername());
        assertEquals("Joao Silva", user.getFullName());
        assertEquals("secret_token", user.getAccessToken());
    }

    @Test
    void testAuthenticateByTagFailure() throws IOException, InterruptedException {
        // Arrange
        String rawTag = "INVALID_TAG";
        when(config.getWorkstationHash()).thenReturn("WS_HASH");
        when(config.url("/login")).thenReturn("http://localhost/api/login");
        when(httpResponse.statusCode()).thenReturn(422);
        when(httpResponse.body()).thenReturn("{\"message\":\"Invalid tag\"}");
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        Optional<User> result = authService.authenticateByTag(rawTag);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testAuthenticateByTagEmptyInput() {
        assertFalse(authService.authenticateByTag("").isPresent());
        assertFalse(authService.authenticateByTag(null).isPresent());
    }
}
