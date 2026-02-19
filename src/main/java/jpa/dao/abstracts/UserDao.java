package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Abstract DAO contract for UserDao.
 */
public abstract class UserDao extends AbstractJpaDao<UUID, User> {

    protected UserDao() {
        super(User.class);
    }

    /**
     * Executes findByEmail operation.
     *
     * @param email method parameter
     * @return operation result
     */
    public abstract Optional<User> findByEmail(String email);
}
