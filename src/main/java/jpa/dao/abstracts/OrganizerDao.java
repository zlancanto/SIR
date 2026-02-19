package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Organizer;

import java.util.UUID;

/**
 * Abstract DAO contract for OrganizerDao.
 */
public abstract class OrganizerDao extends AbstractJpaDao<UUID, Organizer> {

    protected OrganizerDao() {
        super(Organizer.class);
    }
}
