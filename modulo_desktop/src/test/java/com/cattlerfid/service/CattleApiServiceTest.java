package com.cattlerfid.service;

import com.cattlerfid.config.ApiConfig;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.model.Vaccine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CattleApiServiceTest {

    private CattleApiService apiService;

    @Mock
    private ApiConfig config;

    @Mock
    private User user;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        when(user.getAccessToken()).thenReturn("fake_token");
        apiService = new CattleApiService(config, user, httpClient);
    }

    @Test
    void testGetCattleByTagSuccess() throws IOException, InterruptedException {
        // Arrange
        String rfidTag = "TAG_BOI_100";
        String jsonResponse = "{\"rfid_tag\":\"TAG_BOI_100\",\"name\":\"Mimosa\",\"weight\":450.0,\"registration_date\":\"2023-05-20\"}";

        when(config.url("/cattle/" + rfidTag)).thenReturn("http://localhost/api/cattle/" + rfidTag);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        Optional<Cattle> result = apiService.getCattleByTag(rfidTag);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Mimosa", result.get().getName());
        assertEquals(450.0, result.get().getWeight());
    }

    @Test
    void testGetAllCattle() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "[{\"rfid_tag\":\"TAG1\",\"name\":\"A\"},{\"rfid_tag\":\"TAG2\",\"name\":\"B\"}]";

        when(config.url("/cattle")).thenReturn("http://localhost/api/cattle");
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        List<Cattle> result = apiService.getAllCattle();

        // Assert
        assertEquals(2, result.size());
        assertEquals("TAG1", result.get(0).getRfidTag());
        assertEquals("TAG2", result.get(1).getRfidTag());
    }

    @Test
    void testSaveCattleSuccess() throws IOException, InterruptedException {
        // Arrange
        Cattle cattle = new Cattle("NEW_TAG", "New Cow", 100.0, "2023-10-01");
        when(config.url("/cattle")).thenReturn("http://localhost/api/cattle");
        when(httpResponse.statusCode()).thenReturn(201);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        boolean result = apiService.saveCattle(cattle);

        // Assert
        assertTrue(result);
    }

    @Test
    void testSaveVaccineSuccess() throws IOException, InterruptedException {
        // Arrange
        Vaccine vaccine = new Vaccine("1", "TAG_BOI_100", "2023-11-01", "Aftosa", 460.0);
        when(config.url("/vaccines")).thenReturn("http://localhost/api/vaccines");
        when(httpResponse.statusCode()).thenReturn(201);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        boolean result = apiService.saveVaccine(vaccine);

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetVaccinesByCattle() throws IOException, InterruptedException {
        // Arrange
        String rfidTag = "TAG_BOI_100";
        String jsonResponse = "[{\"rfid_tag\":\"TAG_BOI_100\",\"vaccine_type\":\"Aftosa\"}]";

        when(config.url("/vaccines?rfid_tag=" + rfidTag))
                .thenReturn("http://localhost/api/vaccines?rfid_tag=" + rfidTag);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        List<Vaccine> result = apiService.getVaccinesByCattle(rfidTag);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Aftosa", result.get(0).getVaccineType());
    }
}
