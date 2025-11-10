package mx.softdentist.integration;

import jakarta.persistence.EntityManager;
import mx.softdentist.dao.AdministradorDAO;
import mx.softdentist.dao.CitaDAO;
import mx.softdentist.dao.EmpleadoDAO;
import mx.softdentist.dao.MensajeDAO;
import mx.softdentist.dao.PacienteDAO;
import mx.softdentist.persistence.HibernateUtil;

/**
 *
 * @author softdentist
 */
public class ServiceLocator {

    private static EntityManager em;
    private static AdministradorDAO administradorDAO;
    private static PacienteDAO pacienteDAO;
    private static EmpleadoDAO empleadoDAO;
    private static CitaDAO citaDAO;
    private static MensajeDAO mensajeDAO;

    private ServiceLocator() {
    }

    public static EntityManager getEntityManager() {
        if (em == null) {
            em = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return em;
        } else {
            return em;
        }
    }

    public static AdministradorDAO getInstanceAdministradorDAO() {
        if (administradorDAO == null) {
            administradorDAO = new AdministradorDAO(getEntityManager());
            return administradorDAO;
        } else {
            return administradorDAO;
        }
    }

    public static PacienteDAO getInstancePacienteDAO() {
        if (pacienteDAO == null) {
            pacienteDAO = new PacienteDAO(getEntityManager());
            return pacienteDAO;
        } else {
            return pacienteDAO;
        }
    }

    public static EmpleadoDAO getInstanceEmpleadoDAO() {
        if (empleadoDAO == null) {
            empleadoDAO = new EmpleadoDAO(getEntityManager());
            return empleadoDAO;
        } else {
            return empleadoDAO;
        }
    }

    public static CitaDAO getInstanceCitaDAO() {
        if (citaDAO == null) {
            citaDAO = new CitaDAO(getEntityManager());
            return citaDAO;
        } else {
            return citaDAO;
        }
    }

    /**
     * Devuelve la instancia única (Singleton) del DAO para Mensajes.
     * Si no existe, la crea pasándole el EntityManager.
     * @return MensajeDAO
     */
    public static MensajeDAO getInstanceMensajeDAO() {
        if (mensajeDAO == null) {
            mensajeDAO = new MensajeDAO(getEntityManager());
            return mensajeDAO;
        } else {
            return mensajeDAO;
        }
    }
}