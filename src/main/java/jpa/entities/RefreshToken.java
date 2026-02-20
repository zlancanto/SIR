package jpa.entities;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Persisted refresh token entry used for rotation and revocation.
 */
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_user", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_refresh_tokens_token_hash", columnNames = "token_hash")
        }
)
public class RefreshToken extends BaseEntity {

    @Column(name = "token_hash", nullable = false, length = 88, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Returns the stored hash of the raw refresh token.
     *
     * @return token hash
     */
    public String getTokenHash() {
        return tokenHash;
    }

    /**
     * Sets the stored hash of the raw refresh token.
     *
     * @param tokenHash token hash
     */
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    /**
     * Returns expiration instant of this refresh token.
     *
     * @return expiration instant
     */
    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets expiration instant of this refresh token.
     *
     * @param expiresAt expiration instant
     */
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Returns revocation instant, or {@code null} when token is still active.
     *
     * @return revocation instant
     */
    public Instant getRevokedAt() {
        return revokedAt;
    }

    /**
     * Sets revocation instant.
     *
     * @param revokedAt revocation instant
     */
    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    /**
     * Returns user owning this refresh token.
     *
     * @return token owner
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets user owning this refresh token.
     *
     * @param user token owner
     */
    public void setUser(User user) {
        this.user = user;
    }
}
