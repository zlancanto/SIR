package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Concert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public abstract class ConcertDao extends AbstractJpaDao<UUID, Concert> {
    protected abstract List<Concert> findConcertsByDateRange(LocalDateTime start, LocalDateTime end);
}
