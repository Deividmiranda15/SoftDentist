package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Paciente;

import java.util.List;


public class PacienteDAO extends AbstractDAO<Paciente> {
    private final EntityManager entityManager;

    public PacienteDAO(EntityManager em) {
        super(Paciente.class);
        this.entityManager = em;
    }

    public List<Paciente> obtenerTodos(){
        return entityManager
                .createQuery("SELECT a FROM Paciente a", Paciente.class)
                .getResultList();
    }

    public Paciente findByCorreo(String correo) {
        try {
            TypedQuery<Paciente> query = getEntityManager().createQuery(
                    "SELECT p FROM Paciente p WHERE p.correo = :correoParam", Paciente.class);
            query.setParameter("correoParam", correo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<String> obtenerTodosLosCorreos() {
        // JPQL para seleccionar únicamente la columna 'correo' y evitamos los nulos o vacíos para que el envío no falle.
        String jpql = "SELECT p.correo FROM Paciente p WHERE p.correo IS NOT NULL AND p.correo <> ''";

        return getEntityManager().createQuery(jpql, String.class).getResultList();
    }
}