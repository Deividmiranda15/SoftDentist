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
                .createQuery("SELECT a FROM Cita a ORDER BY a.fecha ASC, a.hora ASC", Cita.class)
                .getResultList();
    }

    public void cancelarCita(Integer idCita) {
        Cita cita = getEntityManager().find(Cita.class, idCita);
        if (cita != null) {
            getEntityManager().getTransaction().begin();
            cita.setEstado("Cancelada");
            getEntityManager().merge(cita);
            getEntityManager().getTransaction().commit();
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<Cita> findCitasByPacienteId(int idPaciente) {
        return entityManager
                .createQuery("SELECT c FROM Cita c WHERE c.idPaciente.id = :pacienteId ORDER BY c.fecha DESC", Cita.class)
                .setParameter("pacienteId", idPaciente)
                .getResultList();
    }
}