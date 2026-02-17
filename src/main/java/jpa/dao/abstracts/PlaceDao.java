package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Place;

import java.util.UUID;

public abstract class PlaceDao extends AbstractJpaDao<UUID, Place> {

    protected PlaceDao() {
        super(Place.class);
    }
}
