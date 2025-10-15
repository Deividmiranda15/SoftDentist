package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
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

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}