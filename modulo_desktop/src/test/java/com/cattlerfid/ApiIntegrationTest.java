package com.cattlerfid;

import com.cattlerfid.config.ApiConfig;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.model.Vaccine;
import com.cattlerfid.service.AuthenticationService;
import com.cattlerfid.service.CattleApiService;
import com.cattlerfid.util.RfidGenerator;
import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real integration test hitting the PHP API.
 * Requires the Laravel server to be running at http://127.0.0.1:8000
 * <p>
 * Run only with: mvn test -Dgroups="integration"
 * Skip with:    mvn test -DexcludedGroups="integration"
 */
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiIntegrationTest {

    private ApiConfig apiConfig;
    private HttpClient httpClient;
    private AuthenticationService authService;
    private CattleApiService cattleService;
    private User currentUser;
    private String sharedTestTag;

    @BeforeAll
    void setup() {
        // Carrega explicitamente o .env do modulo_desktop
        apiConfig = new ApiConfig(".env");
        httpClient = com.cattlerfid.config.HttpClientFactory.create(apiConfig);
        authService = new AuthenticationService(apiConfig, httpClient);
        sharedTestTag = RfidGenerator.generateCattleTag();
    }

    @Test
    @Order(1)
    @DisplayName("Should authenticate using veterinarian tag V000002")
    void testAuthentication() {
        // Tag V000002 seeded in PHP
        Optional<User> userOpt = authService.authenticateByTag("V000002");

        assertTrue(userOpt.isPresent(), "Authentication should return a User object");
        currentUser = userOpt.get();

        assertEquals("Vet Integration Test", currentUser.getName());
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
    @DisplayName("Should create a test cattle")
    void testCreateCattle() {
        assertNotNull(cattleService, "Cattle service must be initialized");

        Cattle c = new Cattle(sharedTestTag, "Integration Test Cow", 500.0, "2024-03-08");
        boolean success = cattleService.saveCattle(c);

        assertTrue(success, "Cattle creation should succeed");
    }

    @Test
    @Order(4)
    @DisplayName("Should register a vaccine for a test cattle")
    void testRegisterVaccine() {
        assertNotNull(cattleService, "Cattle service must be initialized");

        // Use a dynamically generated tag
        String testTag = sharedTestTag;

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

    @Test
    @Order(5)
    @DisplayName("Should update an existing cattle's name and weight")
    void testUpdateCattle() {
        assertNotNull(cattleService, "Cattle service must be initialized");

        // We know the cattle exists from previous vaccine registration test
        Cattle c = cattleService.getCattleByTag(sharedTestTag).orElseThrow();
        String originalName = c.getName();
        double originalWeight = c.getWeight();

        c.setName("Updated Mimosa Name");
        c.setWeight(originalWeight + 10.0);

        boolean success = cattleService.updateCattle(c);

        assertTrue(success, "Cattle update should succeed");

        // Verify changes
        Cattle updated = cattleService.getCattleByTag(sharedTestTag).orElseThrow();
        assertEquals("Updated Mimosa Name", updated.getName());
        assertEquals(originalWeight + 10.0, updated.getWeight());
    }
}
