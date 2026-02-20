package jpa.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpa.entities.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation producing HMAC-SHA256 signed JWT access tokens.
 *
 * <p>This component also generates and hashes opaque refresh tokens.</p>
 */
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final byte[] signingKey;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Builds a token service with explicit signing key and TTL values.
     *
     * @param signingKey secret used to sign JWT payloads
     * @param accessTokenTtlSeconds access-token lifetime in seconds
     * @param refreshTokenTtlSeconds refresh-token lifetime in seconds
     */
    public AccessTokenServiceImpl(String signingKey, long accessTokenTtlSeconds, long refreshTokenTtlSeconds) {
        this.signingKey = signingKey.getBytes(StandardCharsets.UTF_8);
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createAccessToken(User user, String role, Instant issuedAt, Instant expiresAt) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("iss", "sir-api");
        payload.put("sub", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("role", role);
        payload.put("iat", issuedAt.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        return sign(payload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateRefreshToken() {
        byte[] bytes = new byte[64];
        RANDOM.nextBytes(bytes);
        return URL_ENCODER.encodeToString(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hashRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken is required");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return BASE64_ENCODER.encodeToString(hashed);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Could not hash refresh token", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRefreshTokenTtlSeconds() {
        return refreshTokenTtlSeconds;
    }

    /**
     * Signs a JWT payload and returns its compact representation.
     *
     * @param payload claims map to serialize
     * @return signed JWT
     */
    private String sign(Map<String, Object> payload) {
        try {
            String headerSegment = encodeJson(Map.of("alg", "HS256", "typ", "JWT"));
            String payloadSegment = encodeJson(payload);
            String unsigned = headerSegment + "." + payloadSegment;

            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(signingKey, HMAC_ALGO));
            byte[] signature = mac.doFinal(unsigned.getBytes(StandardCharsets.UTF_8));
            String signatureSegment = URL_ENCODER.encodeToString(signature);

            return unsigned + "." + signatureSegment;
        } catch (GeneralSecurityException | JsonProcessingException ex) {
            throw new IllegalStateException("Could not generate access token", ex);
        }
    }

    /**
     * Serializes and Base64URL-encodes a JSON object.
     *
     * @param content map to serialize
     * @return encoded segment ready for JWT composition
     * @throws JsonProcessingException when serialization fails
     */
    private String encodeJson(Map<String, Object> content) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(content);
        return URL_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }
}
