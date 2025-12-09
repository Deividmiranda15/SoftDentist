package mx.softdentist.facade;

import java.sql.Timestamp;
import java.util.List;
import mx.softdentist.dao.EmpleadoDAO;
import mx.softdentist.dao.MensajeDAO;
import mx.softdentist.dao.PacienteDAO;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.entidad.Mensaje;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;

public class FacadeMensaje {

    // Prepara y guarda un nuevo mensaje en la base de datos.
    public boolean enviarMensaje(Mensaje mensaje) {
        try {
            MensajeDAO mensajeDAO = ServiceLocator.getInstanceMensajeDAO();

            // Valores por defecto
            mensaje.setFechaEnvio(new Timestamp(System.currentTimeMillis())); // La hora actual
            mensaje.setLeido(false); // Un mensaje nuevo nunca está leído

            // Guardar
            mensajeDAO.save(mensaje);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene el historial de chat completo entre dos usuarios.
     * @param idUsuario1 ID del primer usuario
     * @param tipoUsuario1 paciente o empleado
     * @param idUsuario2 ID del segundo usuario
     * @param tipoUsuario2 paciente o empleado
     * @return La lista de mensajes de la conversación.
     */
    public List<Mensaje> getHistorialChat(Long idUsuario1, String tipoUsuario1, Long idUsuario2, String tipoUsuario2) {
        MensajeDAO mensajeDAO = ServiceLocator.getInstanceMensajeDAO();
        return mensajeDAO.findChatHistory(idUsuario1, tipoUsuario1, idUsuario2, tipoUsuario2);
    }

    /**
     * Marca todos los mensajes de un chat como leídos.
     * Llama a la consulta de actualización masiva en el DAO.
     * @param idReceptor ID del usuario que está leyendo (el usuario actual)
     * @param tipoReceptor Tipo del usuario que está leyendo
     * @param idEmisor ID del usuario que envió los mensajes (el contacto)
     * @param tipoEmisor Tipo del usuario que envió
     */
    public void marcarComoLeidos(Long idReceptor, String tipoReceptor, Long idEmisor, String tipoEmisor) {
        try {
            MensajeDAO mensajeDAO = ServiceLocator.getInstanceMensajeDAO();
            // Llama al nuevo método que añadiremos al DAO
            mensajeDAO.markAsRead(idReceptor, tipoReceptor, idEmisor, tipoEmisor);
        } catch (Exception e) {
            System.err.println("Error al marcar mensajes como leídos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la lista de todos los pacientes para la lista de contactos del Empleado.
     * @return Lista de Pacientes.
     */
    public List<Paciente> getTodosPacientes() {
        PacienteDAO pacienteDAO = ServiceLocator.getInstancePacienteDAO();
        // Usamos el método 'findAll' que está en AbstractDAO
        return pacienteDAO.findAll();
    }

    /**
     * Obtiene la lista de todos los empleados para la lista de contactos del Paciente.
     * @return Lista de Empleados.
     */
    public List<Empleado> getTodosEmpleados() {
        EmpleadoDAO empleadoDAO = ServiceLocator.getInstanceEmpleadoDAO();
        // Usamos el método 'findAll' que está en AbstractDAO
        return empleadoDAO.findAll();
    }
}