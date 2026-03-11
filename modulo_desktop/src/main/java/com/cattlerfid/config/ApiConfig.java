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
    private final boolean trustAllCerts;
    private final String sslDevCertPath;

    /** Construtor de produção: lê .env no diretório de trabalho. */
    public ApiConfig() {
        this(ENV_FILE);
    }

    /** Construtor para testes: lê .env do path informado. */
    public ApiConfig(String envFilePath) {
        Map<String, String> env = loadEnv(envFilePath);
        this.baseUrl = env.getOrDefault("API_BASE_URL", "http://127.0.0.1:8000/api");
        this.workstationHash = env.getOrDefault("API_WORKSTATION_HASH", "");
        this.trustAllCerts = "true".equalsIgnoreCase(env.getOrDefault("SSL_TRUST_ALL", "false"));
        this.sslDevCertPath = env.getOrDefault("SSL_DEV_CERT_PATH", "");
    }

    private Map<String, String> loadEnv(String filePath) {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

    /** Retorna true se SSL_TRUST_ALL=true no .env (aceita self-signed). */
    public boolean isTrustAllCerts() {
        return trustAllCerts;
    }

    public String getSslDevCertPath() {
        return sslDevCertPath;
    }

    /**
     * Convenience: full URL for a given path (e.g. "/login")
     */
    public String url(String path) {
        return baseUrl + path;
    }
}
