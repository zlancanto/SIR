package jpa.services;

import jpa.dao.impl.ConcertDao;

public class ConcertService {
    private final ConcertDao concertDao;

    public ConcertService(ConcertDao concertDao) {
        this.concertDao = concertDao;
    }
}
