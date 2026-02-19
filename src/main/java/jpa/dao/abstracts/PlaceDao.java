package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Place;

import java.util.UUID;

/**
 * Abstract DAO contract for PlaceDao.
 */
public abstract class PlaceDao extends AbstractJpaDao<UUID, Place> {

    protected PlaceDao() {
        super(Place.class);
    }
}
