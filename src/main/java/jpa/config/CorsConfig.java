package jpa.config;

import static jpa.utils.StringValidation.firstNonBlank;

/**
 * Resolves CORS-related runtime configuration.
 */
public final class CorsConfig {

    public static final String ALLOWED_ORIGIN_PROPERTY = "app.cors.allowed-origin";
    public static final String ALLOWED_ORIGIN_ENV = "APP_CORS_ALLOWED_ORIGIN";

    private static final String DEFAULT_ALLOWED_ORIGIN = "http://localhost:5173";

    private CorsConfig() {}

    /**
     * Resolves the allowed frontend origin for CORS.
     *
     * @return non-blank HTTP origin (example: http://localhost:5173)
     */
    public static String resolveAllowedOrigin() {
        String raw = firstNonBlank(
                System.getProperty(ALLOWED_ORIGIN_PROPERTY),
                System.getenv(ALLOWED_ORIGIN_ENV)
        );

        String value = raw == null ? DEFAULT_ALLOWED_ORIGIN : raw.trim();
        if (value.isBlank()) {
            throw new IllegalStateException("CORS allowed origin cannot be blank");
        }

        return value;
    }
}
