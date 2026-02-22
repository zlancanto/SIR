package jpa.security.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.NotAuthorizedException;
import jpa.dto.security.AccessTokenClaimsDto;
import jpa.entities.User;
import jpa.security.interfaces.AccessTokenService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of {@link AccessTokenService} backed by HMAC-SHA256.
 *
 * <p>This component is responsible for signing and verifying access tokens and
 * generating/hashing refresh token values.</p>
 */
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final String TOKEN_ISSUER = "sir-api";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
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
        payload.put("iss", TOKEN_ISSUER);
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
    public AccessTokenClaimsDto verifyAccessToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new NotAuthorizedException("Missing access token");
        }

        String[] segments = rawToken.split("\\.");
        if (segments.length != 3) {
            throw new NotAuthorizedException("Invalid access token");
        }

        String unsignedToken = segments[0] + "." + segments[1];
        byte[] expectedSignature = signBytes(unsignedToken);
        byte[] actualSignature = decodeSignature(segments[2]);

        if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
            throw new NotAuthorizedException("Invalid access token");
        }

        JsonNode payload = decodePayload(segments[1]);
        String issuer = readTextClaim(payload, "iss");
        if (!TOKEN_ISSUER.equals(issuer)) {
            throw new NotAuthorizedException("Invalid access token issuer");
        }

        UUID userId = parseUuid(readTextClaim(payload, "sub"));
        String email = readTextClaim(payload, "email");
        String role = readTextClaim(payload, "role");
        Instant issuedAt = readInstantClaim(payload, "iat");
        Instant expiresAt = readInstantClaim(payload, "exp");

        if (!expiresAt.isAfter(Instant.now())) {
            throw new NotAuthorizedException("Access token expired");
        }

        return new AccessTokenClaimsDto(userId, email, role, issuedAt, expiresAt);
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
            String signatureSegment = URL_ENCODER.encodeToString(signBytes(unsigned));

            return unsigned + "." + signatureSegment;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not generate access token", ex);
        }
    }

    /**
     * Computes a HMAC signature for a token's unsigned part.
     *
     * @param unsignedToken header and payload concatenated by a dot
     * @return raw signature bytes
     */
    private byte[] signBytes(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(signingKey, HMAC_ALGO));
            return mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Could not sign access token", ex);
        }
    }

    /**
     * Decodes the JWT signature segment.
     *
     * @param signatureSegment Base64URL signature segment
     * @return decoded signature bytes
     */
    private byte[] decodeSignature(String signatureSegment) {
        try {
            return URL_DECODER.decode(signatureSegment);
        } catch (IllegalArgumentException ex) {
            throw new NotAuthorizedException("Invalid access token");
        }
    }

    /**
     * Deserializes the JWT payload segment into a JSON object.
     *
     * @param payloadSegment Base64URL payload segment
     * @return JSON payload node
     */
    private JsonNode decodePayload(String payloadSegment) {
        try {
            byte[] payloadBytes = URL_DECODER.decode(payloadSegment);
            return objectMapper.readTree(payloadBytes);
        } catch (IllegalArgumentException | IOException ex) {
            throw new NotAuthorizedException("Invalid access token");
        }
    }

    /**
     * Reads a mandatory textual claim from a payload.
     *
     * @param payload JWT payload
     * @param claimName claim key
     * @return non-empty claim value
     */
    private String readTextClaim(JsonNode payload, String claimName) {
        JsonNode claimNode = payload.get(claimName);
        if (claimNode == null) {
            throw new NotAuthorizedException("Invalid access token");
        }

        String value = claimNode.asText();
        if (value == null || value.isBlank()) {
            throw new NotAuthorizedException("Invalid access token");
        }

        return value;
    }

    /**
     * Reads a mandatory epoch-second claim and converts it to an {@link Instant}.
     *
     * @param payload JWT payload
     * @param claimName claim key
     * @return converted instant value
     */
    private Instant readInstantClaim(JsonNode payload, String claimName) {
        JsonNode claimNode = payload.get(claimName);
        if (claimNode == null || !claimNode.isNumber()) {
            throw new NotAuthorizedException("Invalid access token");
        }

        return Instant.ofEpochSecond(claimNode.asLong());
    }

    /**
     * Parses a UUID claim value.
     *
     * @param uuidValue textual UUID value
     * @return parsed UUID
     */
    private UUID parseUuid(String uuidValue) {
        try {
            return UUID.fromString(uuidValue);
        } catch (IllegalArgumentException ex) {
            throw new NotAuthorizedException("Invalid access token");
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
