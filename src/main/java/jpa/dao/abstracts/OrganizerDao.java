package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Organizer;

import java.util.UUID;

public abstract class OrganizerDao extends AbstractJpaDao<UUID, Organizer> {

    protected OrganizerDao() {
        super(Organizer.class);
    }
}
