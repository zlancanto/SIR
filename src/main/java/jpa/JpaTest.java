package jpa;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class JpaTest {
    private EntityManager manager;

    public JpaTest(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        EntityManager manager = EntityManagerHelper.getEntityManager();

        JpaTest test = new JpaTest(manager);

        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {

            //test.createEmployees();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        tx.commit();
        //test.listEmployees();
        manager.close();
        EntityManagerHelper.closeEntityManagerFactory();
        System.out.println(".. done");
    }
}
