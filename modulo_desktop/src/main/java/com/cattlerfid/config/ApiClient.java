package com.cattlerfid.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Concentrates all API communication logic.
 * Handles logging, JSON serialization/deserialization, and common headers.
 */
public class ApiClient {

    private final ApiConfig config;
    private final HttpClient http;
    private final Gson gson;

    public ApiClient(ApiConfig config) {
        this(config, HttpClientFactory.create(config));
    }

    public ApiClient(ApiConfig config, HttpClient http) {
        this.config = config;
        this.http = http;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Sends a request and returns the response body as a string.
     * Log request and response details.
     */
    public HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        logRequest(request);
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        logResponse(response);
        return response;
    }

    /**
     * Asynchronous version of send.
     */
    public CompletableFuture<HttpResponse<String>> sendAsync(HttpRequest request) {
        logRequest(request);
        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    logResponse(response);
                    return response;
                });
    }

    /**
     * Helper to create a request builder with common headers.
     */
    public HttpRequest.Builder newRequestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(java.net.URI.create(config.url(path)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    /**
     * Helper to create an authenticated request builder.
     */
    public HttpRequest.Builder newAuthenticatedRequestBuilder(String path, String token) {
        return newRequestBuilder(path)
                .header("Authorization", "Bearer " + token);
    }

    public Gson getGson() {
        return gson;
    }

    public ApiConfig getConfig() {
        return config;
    }

    private void logRequest(HttpRequest request) {
        String method = request.method();
        String uri = request.uri().toString();
        String headers = request.headers().map().toString();
        
        // Mask Token in logs
        if (headers.contains("Authorization=[Bearer ")) {
            headers = headers.replaceAll("Authorization=\\[Bearer [^\\]]+\\]", "Authorization=[Bearer ********]");
        }

        System.out.println("\n[API REQUEST]");
        System.out.println("Method: " + method);
        System.out.println("URI:    " + uri);
        System.out.println("Headers: " + headers);
        
        request.bodyPublisher().ifPresent(publisher -> {
            // Body publishing is usually done via String in this app
            // For logging purposes we assume it's small/readable
            // In a real production app we might avoid logging large bodies
        });
    }

    private void logResponse(HttpResponse<String> response) {
        System.out.println("\n[API RESPONSE]");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body:   " + response.body());
        System.out.println("----------------------------------\n");
    }
}
