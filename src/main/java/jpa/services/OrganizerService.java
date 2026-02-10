package jpa.services;

import jpa.dao.impl.OrganizerDao;

public class OrganizerService {
    private final OrganizerDao organizerDao;

    public OrganizerService(OrganizerDao organizerDao) {
        this.organizerDao = organizerDao;
    }
}
