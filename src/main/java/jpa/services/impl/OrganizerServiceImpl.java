package jpa.services.impl;

import jpa.dao.abstracts.OrganizerDao;
import jpa.services.interfaces.OrganizerService;

/**
 * Service implementation OrganizerServiceImpl.
 */
public class OrganizerServiceImpl implements OrganizerService {
    private final OrganizerDao organizerDao;

    /**
     * Creates a new instance of OrganizerServiceImpl.
     *
     * @param organizerDao method parameter
     */
    public OrganizerServiceImpl(OrganizerDao organizerDao) {
        this.organizerDao = organizerDao;
    }
}
