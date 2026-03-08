package com.cattlerfid;

import com.cattlerfid.config.ApiConfig;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.model.Vaccine;
import com.cattlerfid.service.AuthenticationService;
import com.cattlerfid.service.CattleApiService;
import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real integration test hitting the PHP API.
 * Requires the Laravel server to be running at http://127.0.0.1:8000
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiIntegrationTest {

    private ApiConfig apiConfig;
    private HttpClient httpClient;
    private AuthenticationService authService;
    private CattleApiService cattleService;
    private User currentUser;

    @BeforeAll
    void setup() {
        apiConfig = new ApiConfig();
        httpClient = HttpClient.newBuilder().build();
        authService = new AuthenticationService(apiConfig, httpClient);
    }

    @Test
    @Order(1)
    @DisplayName("Should authenticate using veterinarian tag V000002")
    void testAuthentication() {
        // Tag V000002 seeded in PHP
        Optional<User> userOpt = authService.authenticateByTag("V000002");

        assertTrue(userOpt.isPresent(), "Authentication should return a User object");
        currentUser = userOpt.get();

        assertEquals("Vet Integration Test", currentUser.getFullName());
        assertNotNull(currentUser.getAccessToken(), "User should have an access token");
        assertTrue(currentUser.isVeterinarian());

        // Now we can initialize the CattleApiService with the authenticated user
        cattleService = new CattleApiService(apiConfig, currentUser, httpClient);
    }

    @Test
    @Order(2)
    @DisplayName("Should list cattle from the API")
    void testListCattle() {
        assertNotNull(cattleService, "Cattle service must be initialized with authenticated user");

        List<Cattle> cattleList = cattleService.getAllCattle();

        assertNotNull(cattleList, "Should return a list of cattle");
        System.out.println("Integration Test - Cattle Found: " + cattleList.size());
    }

    @Test
    @Order(3)
    @DisplayName("Should register a vaccine for a test cattle")
    void testRegisterVaccine() {
        assertNotNull(cattleService, "Cattle service must be initialized");

        // Use the tag seeded in IntegrationTestDataSeeder
        String testTag = "C000002";

        Vaccine v = new Vaccine();
        v.setRfidTag(testTag);
        v.setVaccineType("BRUCELOSE");
        v.setCurrentWeight(180.0);
        v.setVaccinationDate("2024-03-08");

        boolean success = cattleService.saveVaccine(v);

        // Note: The API will create the cattle if it doesn't exist, so this should
        // return true
        assertTrue(success, "Vaccine registration should succeed");
        System.out.println("Integration Test - Vaccine Registration Result: " + success);
    }
}
