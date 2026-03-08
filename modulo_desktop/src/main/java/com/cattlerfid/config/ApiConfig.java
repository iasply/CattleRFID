package com.cattlerfid.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads configuration from a .env file located at the working directory.
 * Format: KEY=VALUE (lines starting with # are ignored).
 */
public class ApiConfig {

    private static final String ENV_FILE = ".env";

    private final String baseUrl;
    private final String workstationHash;

    public ApiConfig() {
        Map<String, String> env = loadEnv();
        this.baseUrl = env.getOrDefault("API_BASE_URL", "http://127.0.0.1:8000/api");
        this.workstationHash = env.getOrDefault("API_WORKSTATION_HASH", "");
    }

    private Map<String, String> loadEnv() {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("[ApiConfig] .env file not found — using defaults. Error: " + e.getMessage());
        }
        return map;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getWorkstationHash() {
        return workstationHash;
    }

    /** Convenience: full URL for a given path (e.g. "/login") */
    public String url(String path) {
        return baseUrl + path;
    }
}
