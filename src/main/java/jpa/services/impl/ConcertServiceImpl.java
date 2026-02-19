package jpa.services.impl;

import jpa.dao.abstracts.ConcertDao;
import jpa.services.interfaces.ConcertService;

/**
 * Service implementation ConcertServiceImpl.
 */
public class ConcertServiceImpl implements ConcertService {
    private final ConcertDao concertDao;

    /**
     * Creates a new instance of ConcertServiceImpl.
     *
     * @param concertDao method parameter
     */
    public ConcertServiceImpl(ConcertDao concertDao) {
        this.concertDao = concertDao;
    }
}
