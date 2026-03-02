package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.dto.place.ResponsePlaceDto;
import jpa.entities.Place;

import java.util.List;
import java.util.UUID;

/**
 * Abstract DAO contract for PlaceDao.
 */
public abstract class PlaceDao extends AbstractJpaDao<UUID, Place> {

    protected PlaceDao() {
        super(Place.class);
    }

    /**
     * Returns all places as lightweight projection.
     *
     * @return place projections
     */
    public abstract List<ResponsePlaceDto> findAllPlaceProjections();
}
