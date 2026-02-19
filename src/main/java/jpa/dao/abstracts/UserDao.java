package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.User;

import java.util.Optional;
import java.util.UUID;

public abstract class UserDao extends AbstractJpaDao<UUID, User> {

    protected UserDao() {
        super(User.class);
    }

    public abstract Optional<User> findByEmail(String email);
}
