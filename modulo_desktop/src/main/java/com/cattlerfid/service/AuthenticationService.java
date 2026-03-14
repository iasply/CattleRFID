package com.cattlerfid.service;

import com.cattlerfid.config.ApiConfig;
import com.cattlerfid.config.HttpClientFactory;
import com.cattlerfid.model.User;
import com.cattlerfid.util.RfidGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Authenticates a veterinarian by sending the raw RFID tag + workstation hash
 * to the Laravel API (POST /api/login).
 * <p>
 * Replaces the previous hardcoded mock.
 */
public class AuthenticationService {

    private final ApiConfig config;
    private final HttpClient http;
    private final Gson gson = new Gson();

    public AuthenticationService(ApiConfig config) {
        this(config, HttpClientFactory.create(config));
    }

    public AuthenticationService(ApiConfig config, HttpClient http) {
        this.config = config;
        this.http = http;
    }

    /**
     * Sends the raw RFID tag to the API alongside the workstation hash.
     * The API hashes the tag internally and validates against its database.
     *
     * @param rawRfidTag Raw tag content read from the Arduino serial port
     * @return Optional<User> with Bearer token if authenticated, empty otherwise
     */
    public Optional<User> authenticateByTag(String rawRfidTag) {
        if (rawRfidTag == null || rawRfidTag.isBlank()) {
            return Optional.empty();
        }

        if (!RfidGenerator.isVetTag(rawRfidTag)) {
            System.err.println("[AuthenticationService] Tag RFID inválida para login de veterinário: " + rawRfidTag);
            return Optional.empty();
        }

        if (config.getWorkstationHash().isBlank()) {
            System.err.println("[AuthenticationService] API_WORKSTATION_HASH not set in .env");
            return Optional.empty();
        }

        String body = gson.toJson(new LoginRequest(config.getWorkstationHash(), rawRfidTag));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.url("/login")))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            System.out.println("[API Request] POST " + config.url("/login"));
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API Response] Status: " + response.statusCode() + " Body: " + response.body());

            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);

                User user = gson.fromJson(json.getAsJsonObject("user"), User.class);
                user.setAccessToken(json.get("access_token").getAsString());

                return Optional.of(user);
            }

            // 422 = validation error (workstation not found, tag invalid, not a vet, etc.)
            System.err.println("[AuthenticationService] Login refused. Status: "
                    + response.statusCode() + " Body: " + response.body());
            return Optional.empty();

        } catch (IOException | InterruptedException e) {
            System.err.println("[AuthenticationService] API unreachable: " + e.getMessage());
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    // ---- Inner DTO ----

    private record LoginRequest(String workstation, String tag) {
    }
}
