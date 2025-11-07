package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Empleado;
import jakarta.persistence.TypedQuery;
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

    public Empleado findByCorreo(String correo) {
        try {
            // La consulta usa 'e.correo' que coincide con el campo en tu Empleado.java
            TypedQuery<Empleado> query = getEntityManager().createQuery(
                    "SELECT e FROM Empleado e WHERE e.correo = :correoParam", Empleado.class);
            query.setParameter("correoParam", correo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            // Si no se encuentra, devuelve null.
            return null;
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}