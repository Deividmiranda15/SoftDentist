package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mx.softdentist.entidad.Pago;
import mx.softdentist.persistence.AbstractDAO;

import java.util.List;

public class PagoDAO extends AbstractDAO<Pago> {
    private final EntityManager entityManager;

    public PagoDAO(EntityManager em) {
        super(Pago.class);
        this.entityManager = em;
    }

    public List<Pago> obtenerTodos(){
        return entityManager
//                .createQuery("SELECT p FROM Pago p ORDER BY p.fecha ASC", Pago.class)
                .createQuery("SELECT p FROM Pago p", Pago.class)
                .getResultList();
    }

//    public Pago obtenerPorCitaId(int idCita) {
//        try {
//            TypedQuery<Pago> query = getEntityManager().createQuery(
//                    "SELECT p FROM Pago p WHERE p.id = :idCitaParam ORDER BY p.fecha DESC", Pago.class);
//            query.setParameter("idCitaParam", idCita);
//            return query.getSingleResult();
//        } catch (NoResultException e) {
//            return null;
//        }
//    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}