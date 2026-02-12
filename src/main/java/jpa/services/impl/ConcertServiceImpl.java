package jpa.services.impl;

import jpa.dao.abstracts.ConcertDao;
import jpa.services.interfaces.ConcertService;

public class ConcertServiceImpl implements ConcertService {
    private final ConcertDao concertDao;

    public ConcertServiceImpl(ConcertDao concertDao) {
        this.concertDao = concertDao;
    }
}
