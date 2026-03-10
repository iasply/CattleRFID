package com.cattlerfid.config;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;
import java.time.Duration;

/**
 * Factory para criação do HttpClient com suporte a SSL.
 *
 * <p>Por padrão usa o SSLContext do sistema (confia em CAs conhecidas,
 * incluindo Let's Encrypt). Quando {@code SSL_TRUST_ALL=true} estiver
 * definido no .env, aceita qualquer certificado — útil para self-signed
 * em ambiente de desenvolvimento/testes.
 *
 * <p><b>⚠ Nunca use SSL_TRUST_ALL=true em produção.</b>
 */
public class HttpClientFactory {

    private HttpClientFactory() {}

    /**
     * Cria um HttpClient configurado conforme as flags do {@link ApiConfig}.
     *
     * @param config configuração da API (lê SSL_TRUST_ALL)
     * @return HttpClient pronto para uso
     */
    public static HttpClient create(ApiConfig config) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL);

        if (config.isTrustAllCerts()) {
            System.err.println("[HttpClientFactory] ⚠  SSL_TRUST_ALL=true — " +
                    "certificate validation disabled. Use only for development!");
            builder.sslContext(buildTrustAllContext());
        }

        return builder.build();
    }

    // ── Trust-all SSLContext (apenas dev/self-signed) ─────────────────────
    private static SSLContext buildTrustAllContext() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAll, new java.security.SecureRandom());
            return ctx;
        } catch (Exception e) {
            throw new RuntimeException("[HttpClientFactory] Failed to build trust-all SSLContext", e);
        }
    }
}
