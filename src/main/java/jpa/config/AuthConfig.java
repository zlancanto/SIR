package jpa.config;

/**
 * Resolves authentication-related runtime configuration.
 *
 * <p>Values are resolved with this precedence:
 * Java system property, then environment variable, then default value when supported.</p>
 */
public final class AuthConfig {

    /**
     * System property holding the JWT signing key.
     */
    public static final String JWT_SIGNING_KEY_PROPERTY = "app.auth.jwt.signing.key";

    /**
     * Environment variable holding the JWT signing key.
     */
    public static final String JWT_SIGNING_KEY_ENV = "APP_AUTH_JWT_SIGNING_KEY";

    /**
     * System property holding access-token TTL in seconds.
     */
    public static final String ACCESS_TOKEN_TTL_PROPERTY = "app.auth.access-token.ttl.seconds";

    /**
     * Environment variable holding access-token TTL in seconds.
     */
    public static final String ACCESS_TOKEN_TTL_ENV = "APP_AUTH_ACCESS_TOKEN_TTL_SECONDS";

    /**
     * System property holding refresh-token TTL in seconds.
     */
    public static final String REFRESH_TOKEN_TTL_PROPERTY = "app.auth.refresh-token.ttl.seconds";

    /**
     * Environment variable holding refresh-token TTL in seconds.
     */
    public static final String REFRESH_TOKEN_TTL_ENV = "APP_AUTH_REFRESH_TOKEN_TTL_SECONDS";

    private static final long DEFAULT_ACCESS_TOKEN_TTL_SECONDS = 900L;
    private static final long DEFAULT_REFRESH_TOKEN_TTL_SECONDS = 2_592_000L;
    private static final int MIN_SIGNING_KEY_LENGTH = 32;

    private AuthConfig() {}

    /**
     * Resolves the JWT signing key and validates minimum entropy length.
     *
     * @return non-blank signing key
     * @throws IllegalStateException when no key is configured or key length is too short
     */
    public static String resolveJwtSigningKey() {
        String key = firstNonBlank(
                System.getProperty(JWT_SIGNING_KEY_PROPERTY),
                System.getenv(JWT_SIGNING_KEY_ENV),
                System.getProperty(AdminConfig.ADMIN_REGISTRATION_KEY_PROPERTY),
                System.getenv(AdminConfig.ADMIN_REGISTRATION_KEY_ENV)
        );

        if (key == null) {
            throw new IllegalStateException(
                    "Missing JWT signing key. Configure " + JWT_SIGNING_KEY_PROPERTY + " or " + JWT_SIGNING_KEY_ENV
            );
        }

        String trimmed = key.trim();
        if (trimmed.length() < MIN_SIGNING_KEY_LENGTH) {
            throw new IllegalStateException("JWT signing key must contain at least " + MIN_SIGNING_KEY_LENGTH + " chars");
        }

        return trimmed;
    }

    /**
     * Resolves access-token TTL in seconds.
     *
     * @return strictly positive TTL value
     */
    public static long resolveAccessTokenTtlSeconds() {
        return resolvePositiveLong(
                ACCESS_TOKEN_TTL_PROPERTY,
                ACCESS_TOKEN_TTL_ENV,
                DEFAULT_ACCESS_TOKEN_TTL_SECONDS,
                "access token TTL"
        );
    }

    /**
     * Resolves refresh-token TTL in seconds.
     *
     * @return strictly positive TTL value
     */
    public static long resolveRefreshTokenTtlSeconds() {
        return resolvePositiveLong(
                REFRESH_TOKEN_TTL_PROPERTY,
                REFRESH_TOKEN_TTL_ENV,
                DEFAULT_REFRESH_TOKEN_TTL_SECONDS,
                "refresh token TTL"
        );
    }

    /**
     * Resolves and validates a positive long value from property/env/default.
     *
     * @param propertyName property key to read first
     * @param envName environment variable to read second
     * @param defaultValue fallback value when no property/env is set
     * @param label human-readable label used in error messages
     * @return strictly positive long value
     */
    private static long resolvePositiveLong(
            String propertyName,
            String envName,
            long defaultValue,
            String label
    ) {
        String raw = firstNonBlank(System.getProperty(propertyName), System.getenv(envName));
        if (raw == null) {
            return defaultValue;
        }

        try {
            long value = Long.parseLong(raw.trim());
            if (value <= 0L) {
                throw new IllegalStateException(label + " must be > 0");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(label + " must be a valid number", ex);
        }
    }

    /**
     * Returns the first non-blank value among ordered candidates.
     *
     * @param values ordered candidates
     * @return first non-blank value, or {@code null} if none is set
     */
    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
