package hansanhha.transaction;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Component
public class JpaTransaction {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public void commit(SimpleEntity simpleEntity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(simpleEntity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }

    public SimpleEntity findByJPQL(String jpql, Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        SimpleEntity find = null;

        try {
            transaction.begin();
            TypedQuery<SimpleEntity> query = entityManager.createQuery(jpql, SimpleEntity.class);
            query.setParameter("id", id);

            find = query.getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            entityManager.close();
        }

        return find;
    }

}
