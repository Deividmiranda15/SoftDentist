package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Cita;
import mx.softdentist.persistence.HibernateUtil;
import org.hibernate.Session;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class CitaDAO extends AbstractDAO<Cita> {
    private final EntityManager entityManager;

    public CitaDAO(EntityManager em) {
        super(Cita.class);
        this.entityManager = em;
    }

    public List<Cita> obtenerTodos() {
        return entityManager
                .createQuery("SELECT a FROM Cita a ORDER BY a.fecha ASC, a.hora ASC", Cita.class)
                .getResultList();
    }

    public List<Cita> obtenerPorPaciente(Integer idPaciente) {
        return entityManager.createQuery(
                        "SELECT c FROM Cita c WHERE c.idPaciente.id = :idPaciente ORDER BY c.fecha DESC, c.hora DESC",
                        Cita.class)
                .setParameter("idPaciente", idPaciente)
                .getResultList();
    }

    public List<LocalTime> obtenerHorasOcupadas(LocalDate fecha) {
        try {
            return entityManager.createQuery(
                            "SELECT c.hora FROM Cita c WHERE c.fecha = :fecha",
                            LocalTime.class
                    )
                    .setParameter("fecha", fecha)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void cancelarCita(Integer idCita) {
        Cita cita = getEntityManager().find(Cita.class, idCita);
        if (cita != null) {
            getEntityManager().getTransaction().begin();
            cita.setEstado(Cita.EstadoCita.valueOf("Cancelada"));
            getEntityManager().merge(cita);
            getEntityManager().getTransaction().commit();
        }
    }

    public void update(Cita cita) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(cita);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
    public List<Cita> obtenerTodasConPacientes() {
        return entityManager
                .createQuery("SELECT c FROM Cita c " +
                        "LEFT JOIN FETCH c.idPaciente " +
                        "LEFT JOIN FETCH c.idEmpleado " +
                        "ORDER BY c.fecha ASC, c.hora ASC", Cita.class)
                .getResultList();
    }

    public List<Cita> obtenerPorPacienteConDetalles(Integer idPaciente) {
        return entityManager.createQuery(
                        "SELECT c FROM Cita c " +
                                "LEFT JOIN FETCH c.idPaciente " +
                                "LEFT JOIN FETCH c.idEmpleado " +
                                "WHERE c.idPaciente.id = :idPaciente " +
                                "ORDER BY c.fecha DESC, c.hora DESC",
                        Cita.class)
                .setParameter("idPaciente", idPaciente)
                .getResultList();
    }
    public List<Cita> findCitasByPacienteId(int idPaciente) {
        return entityManager
                .createQuery("SELECT c FROM Cita c WHERE c.idPaciente.id = :pacienteId ORDER BY c.fecha DESC", Cita.class)
                .setParameter("pacienteId", idPaciente)
                .getResultList();
    }
}