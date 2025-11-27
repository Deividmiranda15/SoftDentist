package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery; // Import para consultas tipadas
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Mensaje;
import jakarta.persistence.Query;

import java.util.List;

public class MensajeDAO extends AbstractDAO<Mensaje> {

    // Guardamos el EntityManager que nos pasa el ServiceLocator
    private final EntityManager entityManager;

    public MensajeDAO(EntityManager em) {
        super(Mensaje.class); // Le decimos a AbstractDAO qué clase manejamos
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Busca el historial de chat completo entre dos usuarios específicos,
     * ordenado por fecha de envío (ascendente).
     *
     * @param idUsuario1 ID del primer usuario (Paciente o Empleado)
     * @param tipoUsuario1 Tipo del primer usuario ("PACIENTE" o "EMPLEADO")
     * @param idUsuario2 ID del segundo usuario (Paciente o Empleado)
     * @param tipoUsuario2 Tipo del segundo usuario ("PACIENTE" o "EMPLEADO")
     * @return Una lista de Mensajes que forman la conversación.
     */
    public List<Mensaje> findChatHistory(Long idUsuario1, String tipoUsuario1, Long idUsuario2, String tipoUsuario2) {

        // (Usuario1 envió a Usuario2) O (Usuario2 envió a Usuario1)
        String jpql = "SELECT m FROM Mensaje m WHERE " +
                "((m.idEmisor = :id1 AND m.tipoEmisor = :tipo1 AND m.idReceptor = :id2 AND m.tipoReceptor = :tipo2) OR " +
                "(m.idEmisor = :id2 AND m.tipoEmisor = :tipo2 AND m.idReceptor = :id1 AND m.tipoReceptor = :tipo1)) " +
                "ORDER BY m.fechaEnvio ASC"; // Ordenados por fecha

        // Consulta tipada (TypedQuery) usando el entityManager
        TypedQuery<Mensaje> query = entityManager.createQuery(jpql, Mensaje.class);

        // evitar inyección SQL
        query.setParameter("id1", idUsuario1);
        query.setParameter("tipo1", tipoUsuario1);
        query.setParameter("id2", idUsuario2);
        query.setParameter("tipo2", tipoUsuario2);

        // Ejecutamos la consulta y devolvemos la lista de mensajes
        return query.getResultList();
    }

    public int markAsRead(Long idReceptor, String tipoReceptor, Long idEmisor, String tipoEmisor) {

        // Usamos el helper 'execute' heredado de AbstractDAO
        return execute(em -> {

            // Creamos una consulta JPQL de ACTUALIZACIÓN (UPDATE)
            String jpql = "UPDATE Mensaje m SET m.leido = true WHERE " +
                    "m.idReceptor = :idReceptor AND m.tipoReceptor = :tipoReceptor AND " +
                    "m.idEmisor = :idEmisor AND m.tipoEmisor = :tipoEmisor AND " +
                    "m.leido = false"; // Solo actualiza los no leídos

            // Si es un Update, se usa "CreateQuery"
            Query query = em.createQuery(jpql);

            // Asignar parámetros
            query.setParameter("idReceptor", idReceptor);
            query.setParameter("tipoReceptor", tipoReceptor);
            query.setParameter("idEmisor", idEmisor);
            query.setParameter("tipoEmisor", tipoEmisor);

            // executeUpdate() corre la consulta y devuelve el número de filas afectadas
            return query.executeUpdate();
        });
    }

}