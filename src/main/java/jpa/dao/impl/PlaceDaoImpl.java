package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jpa.dao.abstracts.PlaceDao;
import jpa.dto.place.ResponsePlaceDto;

import java.util.List;

/**
 * JPA DAO implementation for PlaceDaoImpl.
 */
public class PlaceDaoImpl extends PlaceDao {

    /**
     * Returns all places using JPQL projection.
     *
     * @return place projections
     */
    @Override
    public List<ResponsePlaceDto> findAllPlaceProjections() {
        EntityManager em = getEntityManager();
        String jpql = """
                SELECT new jpa.dto.place.ResponsePlaceDto(
                    p.id,
                    p.name,
                    p.address,
                    p.city,
                    p.zipCode,
                    p.capacity
                )
                FROM Place p
                ORDER BY p.name ASC
                """;

        return em.createQuery(jpql, ResponsePlaceDto.class)
                .getResultList();
    }
}
