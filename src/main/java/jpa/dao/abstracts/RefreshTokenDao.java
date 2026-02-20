package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO contract for refresh token persistence and lookup.
 */
public abstract class RefreshTokenDao extends AbstractJpaDao<UUID, RefreshToken> {

    protected RefreshTokenDao() {
        super(RefreshToken.class);
    }

    /**
     * Finds an active refresh token by hash.
     *
     * @param tokenHash hashed token value
     * @param now current instant used for expiration filtering
     * @return optional active token
     */
    public abstract Optional<RefreshToken> findValidByHash(String tokenHash, Instant now);
}
