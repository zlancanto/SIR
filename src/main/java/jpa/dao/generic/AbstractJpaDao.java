package jpa.dao.generic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractJpaDao<K, T extends Serializable> implements IGenericDao<K, T> {

    private final Class<T> clazz;

    protected AbstractJpaDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected EntityManager getEntityManager() {
        return EntityManagerHelper.getEntityManager();
    }

    @Override
    public T findOne(K id) {
        return getEntityManager().find(clazz, id);
    }

    @Override
    public List<T> findAll() {
        String jpql = "select e from " + clazz.getSimpleName() + " e";
        return getEntityManager().createQuery(jpql, clazz).getResultList();
    }

    @Override
    public void save(T entity) {
        executeInTransaction(em -> {
            em.persist(entity);
            return null;
        });
    }

    @Override
    public T update(final T entity) {
        return executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(T entity) {
        if (entity == null) {
            return;
        }

        executeInTransaction(em -> {
            T managedEntity = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managedEntity);
            return null;
        });
    }

    @Override
    public void deleteById(K entityId) {
        T entity = findOne(entityId);
        delete(entity);
    }

    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }
}
