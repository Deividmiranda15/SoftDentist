package mx.softdentist.delegate;

import java.util.List;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.entidad.Mensaje;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.facade.FacadeMensaje;
import mx.softdentist.integration.ServiceFacadeLocator;

public class DelegateMensaje {

    // Delega la acción de enviar el emensaje al facade
    public boolean enviarMensaje(Mensaje mensaje) {
        FacadeMensaje facadeMensaje = ServiceFacadeLocator.getInstanceFacadeMensaje();
        return facadeMensaje.enviarMensaje(mensaje);
    }

    // Delega la obtención del historial de chat al Facade.
    public List<Mensaje> getHistorialChat(Long idUsuario1, String tipoUsuario1, Long idUsuario2, String tipoUsuario2) {
        FacadeMensaje facadeMensaje = ServiceFacadeLocator.getInstanceFacadeMensaje();
        return facadeMensaje.getHistorialChat(idUsuario1, tipoUsuario1, idUsuario2, tipoUsuario2);
    }


     // Delega la acción de marcar mensajes como leídos.
    public void marcarComoLeidos(Long idReceptor, String tipoReceptor, Long idEmisor, String tipoEmisor) {
        FacadeMensaje facadeMensaje = ServiceFacadeLocator.getInstanceFacadeMensaje();
        facadeMensaje.marcarComoLeidos(idReceptor, tipoReceptor, idEmisor, tipoEmisor);
    }

    // Delega la obtención de todos los pacientes.
    public List<Paciente> getTodosPacientes() {
        FacadeMensaje facadeMensaje = ServiceFacadeLocator.getInstanceFacadeMensaje();
        return facadeMensaje.getTodosPacientes();
    }

    // Delega la obtención de todos los empleados.
    public List<Empleado> getTodosEmpleados() {
        FacadeMensaje facadeMensaje = ServiceFacadeLocator.getInstanceFacadeMensaje();
        return facadeMensaje.getTodosEmpleados();
    }
}