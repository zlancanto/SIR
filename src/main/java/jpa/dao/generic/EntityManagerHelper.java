package jpa.dao.generic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Generic DAO infrastructure component EntityManagerHelper.
 */
public class EntityManagerHelper {

    private static final EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal;

    static {
        emf = Persistence.createEntityManagerFactory("dev");
        threadLocal = new ThreadLocal<EntityManager>();
    }

    /**
     * Executes getEntityManager operation.
     *
     * @return operation result
     */
    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();

        if (em == null) {
            em = emf.createEntityManager();
            threadLocal.set(em);
        }
        return em;
    }

    /**
     * Executes closeEntityManager operation.
     */
    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null) {
            em.close();
            threadLocal.set(null);
        }
    }

    /**
     * Executes closeEntityManagerFactory operation.
     */
    public static void closeEntityManagerFactory() {
        emf.close();
    }

    /**
     * Executes beginTransaction operation.
     */
    public static void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    /**
     * Executes rollback operation.
     */
    public static void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    /**
     * Executes commit operation.
     */
    public static void commit() {
        getEntityManager().getTransaction().commit();
    }
}
