package jpa.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jpa.dao.abstracts.ConcertDao;
import jpa.entities.Concert;

import java.time.LocalDateTime;
import java.util.List;

public class ConcertDaoImpl extends ConcertDao {

    @Override
    public List<Concert> findConcertsByDateRange(LocalDateTime start, LocalDateTime end) {
        // 1. Initialisation du CriteriaBuilder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 2. Création de la requête typée
        CriteriaQuery<Concert> cq = cb.createQuery(Concert.class);

        // 3. Définition de la racine (FROM Concert)
        Root<Concert> concert = cq.from(Concert.class);

        // 4. Construction de la clause WHERE (BETWEEN start AND end)
        Predicate dateRangePredicate = cb.between(concert.get("date"), start, end);

        // 5. Application du filtre et tri par date
        cq.where(dateRangePredicate);
        cq.orderBy(cb.asc(concert.get("date")));

        // 6. Exécution de la requête
        return entityManager.createQuery(cq).getResultList();
    }
}
