package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Admin;

import java.util.UUID;

/**
 * Abstract DAO contract for AdminDao.
 */
public abstract class AdminDao extends AbstractJpaDao<UUID, Admin> {

    protected AdminDao() {
        super(Admin.class);
    }
}
