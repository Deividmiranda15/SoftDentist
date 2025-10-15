package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Cita;

import java.util.List;


public class CitaDAO extends AbstractDAO<Cita> {
    private final EntityManager entityManager;

    public CitaDAO(EntityManager em) {
        super(Cita.class);
        this.entityManager = em;
    }

    public List<Cita> obtenerTodos(){
        return entityManager
                .createQuery("SELECT a FROM Cita a", Cita.class)
                .getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}