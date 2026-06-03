package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
                            "SELECT c.hora FROM Cita c WHERE c.fecha = :fecha AND c.estado <> :cancelada",
                            LocalTime.class)
                    .setParameter("fecha", fecha)
                    .setParameter("cancelada", Cita.EstadoCita.Cancelada)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<LocalTime> obtenerHorasOcupadasExcluyendo(LocalDate fecha, Integer idCita) {
        try {
            return entityManager.createQuery(
                            "SELECT c.hora FROM Cita c " +
                                    "WHERE c.fecha = :fecha " +
                                    "AND c.id != :idCita " +
                                    "AND c.estado NOT IN (:cancelada, :completada)",
                            LocalTime.class)
                    .setParameter("fecha", fecha)
                    .setParameter("idCita", idCita)
                    .setParameter("cancelada", Cita.EstadoCita.Cancelada)
                    .setParameter("completada", Cita.EstadoCita.Completada)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void cancelarCita(Integer idCita) {
        try {
            entityManager.getTransaction().begin();
            Cita cita = entityManager.find(Cita.class, idCita);
            if (cita != null) {
                cita.setEstado(Cita.EstadoCita.Cancelada);
                entityManager.merge(cita);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive())
                entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public void update(Cita cita) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(cita);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive())
                entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public boolean reagendarCita(Integer idCita, LocalDate nuevaFecha, LocalTime nuevaHora, String motivo) {
        try {
            List<LocalTime> ocupadas = obtenerHorasOcupadasExcluyendo(nuevaFecha, idCita);

            boolean ocupada = ocupadas.stream()
                    .anyMatch(h -> h.getHour() == nuevaHora.getHour()
                            && h.getMinute() == nuevaHora.getMinute());

            if (ocupada) {
                System.out.println("La hora " + nuevaHora + " ya está tomada por otra cita.");
                return false;
            }

            entityManager.getTransaction().begin();
            Cita cita = entityManager.find(Cita.class, idCita);

            if (cita != null) {
                cita.setFechaAnterior(cita.getFecha());
                cita.setFecha(nuevaFecha);
                cita.setHora(nuevaHora);
                cita.setMotivo(motivo);

                cita.setEstado(Cita.EstadoCita.Pendiente);

                entityManager.merge(cita);
                entityManager.getTransaction().commit();
                return true;
            }

            return false;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            e.printStackTrace();
            return false;
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