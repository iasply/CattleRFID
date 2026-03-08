package com.cattlerfid.service;

import com.cattlerfid.util.RfidGenerator;

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
        String rfidTag = RfidGenerator.generateCattleTag();
        String jsonResponse = "{\"rfid_tag\":\"" + rfidTag
                + "\",\"name\":\"Mimosa\",\"weight\":450.0,\"registration_date\":\"2023-05-20\"}";

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
        String tag1 = RfidGenerator.generateCattleTag();
        String tag2 = RfidGenerator.generateCattleTag();
        String jsonResponse = "{ \"data\": [{\"rfid_tag\":\"" + tag1 + "\",\"name\":\"A\"},{\"rfid_tag\":\"" + tag2
                + "\",\"name\":\"B\"}] }";

        when(config.url("/cattle")).thenReturn("http://localhost/api/cattle");
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        List<Cattle> result = apiService.getAllCattle();

        // Assert
        assertEquals(2, result.size());
        assertEquals(tag1, result.get(0).getRfidTag());
        assertEquals(tag2, result.get(1).getRfidTag());
    }

    @Test
    void testSaveCattleSuccess() throws IOException, InterruptedException {
        // Arrange
        String newTag = RfidGenerator.generateCattleTag();
        Cattle cattle = new Cattle(newTag, "New Cow", 100.0, "2023-10-01");
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
        String vaccineTag = RfidGenerator.generateCattleTag();
        Vaccine vaccine = new Vaccine("1", vaccineTag, "2023-11-01", "Aftosa", 460.0);
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
        String rfidTag = RfidGenerator.generateCattleTag();
        String jsonResponse = "{ \"data\": [{\"rfid_tag\":\"" + rfidTag + "\",\"vaccine_type\":\"Aftosa\"}] }";

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

    @Test
    void testGetAllCattleWithVaccines() throws IOException, InterruptedException {
        // Arrange
        String tag1 = RfidGenerator.generateCattleTag();
        String tag2 = RfidGenerator.generateCattleTag();
        String jsonResponse = "{ \"data\": [{\"rfid_tag\":\"" + tag1
                + "\",\"name\":\"A\",\"vaccines_count\":2},{\"rfid_tag\":\"" + tag2
                + "\",\"name\":\"B\",\"vaccines_count\":0}] }";

        when(config.url("/cattle-with-vaccines")).thenReturn("http://localhost/api/cattle-with-vaccines");
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);

        // Act
        List<Cattle> result = apiService.getAllCattleWithVaccines();

        // Assert
        assertEquals(2, result.size());
        assertEquals(tag1, result.get(0).getRfidTag());
        assertEquals(2, result.get(0).getVaccinesCount());
        assertEquals(tag2, result.get(1).getRfidTag());
        assertEquals(0, result.get(1).getVaccinesCount());
    }
}
