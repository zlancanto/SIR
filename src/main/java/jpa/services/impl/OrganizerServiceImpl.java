package jpa.services.impl;

import jpa.dao.abstracts.OrganizerDao;
import jpa.services.interfaces.OrganizerService;

public class OrganizerServiceImpl implements OrganizerService {
    private final OrganizerDao organizerDao;

    public OrganizerServiceImpl(OrganizerDao organizerDao) {
        this.organizerDao = organizerDao;
    }
}
