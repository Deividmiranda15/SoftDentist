package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Empleado;

import java.util.List;


public class EmpleadoDAO extends AbstractDAO<Empleado> {
    private final EntityManager entityManager;

    public EmpleadoDAO(EntityManager em) {
        super(Empleado.class);
        this.entityManager = em;
    }

    public List<Empleado> obtenerTodos(){
        return entityManager
                .createQuery("SELECT a FROM Empleado a", Empleado.class)
                .getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}