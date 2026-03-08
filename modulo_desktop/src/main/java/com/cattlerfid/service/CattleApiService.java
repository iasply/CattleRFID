package com.cattlerfid.service;

import com.cattlerfid.config.ApiConfig;
import com.cattlerfid.model.Cattle;
import com.cattlerfid.model.User;
import com.cattlerfid.model.Vaccine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Communicates with the Laravel API for Cattle and Vaccine operations.
 * Requires an authenticated User (with Bearer token).
 */
public class CattleApiService {

    private final ApiConfig config;
    private final User user;
    private final HttpClient http;
    private final Gson gson = new Gson();

    public CattleApiService(ApiConfig config, User user) {
        this(config, user, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build());
    }

    public CattleApiService(ApiConfig config, User user, HttpClient http) {
        this.config = config;
        this.user = user;
        this.http = http;
    }

    /**
     * Finds a single cattle by its RFID tag content using the new show-by-tag
     * endpoint.
     */
    public Optional<Cattle> getCattleByTag(String rfidTag) {
        if (rfidTag == null || rfidTag.isBlank())
            return Optional.empty();

        HttpRequest request = authenticatedRequest("/cattle/" + rfidTag)
                .GET()
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Optional.of(gson.fromJson(response.body(), Cattle.class));
            }
        } catch (IOException | InterruptedException e) {
            handleError("Error fetching cattle by tag", e);
        }
        return Optional.empty();
    }

    /**
     * Lists all cattle registered in the system.
     */
    public List<Cattle> getAllCattle() {
        HttpRequest request = authenticatedRequest("/cattle")
                .GET()
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<Cattle>>() {
                }.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (IOException | InterruptedException e) {
            handleError("Error fetching all cattle", e);
        }
        return new ArrayList<>();
    }

    /**
     * Persists new cattle data to the cloud.
     */
    public boolean saveCattle(Cattle cattle) {
        String body = gson.toJson(cattle);
        HttpRequest request = authenticatedRequest("/cattle")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            handleError("Error saving cattle", e);
            return false;
        }
    }

    /**
     * Records a new vaccination event.
     */
    public boolean saveVaccine(Vaccine vaccine) {
        String body = gson.toJson(vaccine);
        HttpRequest request = authenticatedRequest("/vaccines")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            handleError("Error saving vaccine", e);
            return false;
        }
    }

    /**
     * Lists vaccines applied to a specific animal.
     */
    public List<Vaccine> getVaccinesByCattle(String rfidTag) {
        if (rfidTag == null || rfidTag.isBlank())
            return new ArrayList<>();

        HttpRequest request = authenticatedRequest("/vaccines?rfid_tag=" + rfidTag)
                .GET()
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<Vaccine>>() {
                }.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (IOException | InterruptedException e) {
            handleError("Error fetching vaccines for tag: " + rfidTag, e);
        }
        return new ArrayList<>();
    }

    // --- Helper Methods ---

    private HttpRequest.Builder authenticatedRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(config.url(path)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + user.getAccessToken())
                .timeout(Duration.ofSeconds(10));
    }

    private void handleError(String message, Exception e) {
        System.err.println("[CattleApiService] " + message + ": " + e.getMessage());
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
