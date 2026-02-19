package jpa.dao.generic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * Generic DAO infrastructure component AbstractJpaDao.
 */
public abstract class AbstractJpaDao<K, T extends Serializable> implements IGenericDao<K, T> {

    private final Class<T> clazz;

    protected AbstractJpaDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected EntityManager getEntityManager() {
        return EntityManagerHelper.getEntityManager();
    }

    /**
     * Executes findOne operation.
     *
     * @param id method parameter
     * @return operation result
     */
    @Override
    public T findOne(K id) {
        return getEntityManager().find(clazz, id);
    }

    /**
     * Executes findAll operation.
     *
     * @return operation result
     */
    @Override
    public List<T> findAll() {
        String jpql = "select e from " + clazz.getSimpleName() + " e";
        return getEntityManager().createQuery(jpql, clazz).getResultList();
    }

    /**
     * Executes save operation.
     *
     * @param entity method parameter
     */
    @Override
    public void save(T entity) {
        executeInTransaction(em -> {
            em.persist(entity);
            return null;
        });
    }

    /**
     * Executes update operation.
     *
     * @param entity method parameter
     * @return operation result
     */
    @Override
    public T update(final T entity) {
        return executeInTransaction(em -> em.merge(entity));
    }

    /**
     * Executes delete operation.
     *
     * @param entity method parameter
     */
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

    /**
     * Executes deleteById operation.
     *
     * @param entityId method parameter
     */
    @Override
    public void deleteById(K entityId) {
        T entity = findOne(entityId);
        delete(entity);
    }

    protected <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            // Centralized transaction handling keeps DAO methods focused on persistence logic.
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            // Roll back on any runtime failure to avoid partial writes.
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }
}
