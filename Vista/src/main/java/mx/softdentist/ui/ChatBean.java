package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext; // Import para leer la sesión
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import mx.softdentist.delegate.DelegateMensaje;
import mx.softdentist.entidad.Administrador;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.entidad.Mensaje;
import mx.softdentist.entidad.Paciente;

@Named(value = "chatBean")
@ViewScoped
public class ChatBean implements Serializable {

    // Variables de estado
    private DelegateMensaje delegateMensaje;
    private List<Paciente> listaPacientes;
    private List<Empleado> listaEmpleados;
    private List<Mensaje> chatHistory;
    private String nuevoMensaje;
    private Long currentUserId;
    private String currentUserType;
    private Long contactoSeleccionadoId;
    private String contactoSeleccionadoTipo;
    private String contactoSeleccionadoNombre;

    // Para saber si es empleado o paciente y así hacer la UI
    private boolean esPaciente;
    private boolean esEmpleado;


    public ChatBean() {
        this.delegateMensaje = new DelegateMensaje();
    }

    @PostConstruct
    public void init() {
        // Obtener el usuario que está logeado (sea paciente o empleado)
        Object usuarioLogueado = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuarioLogueado");

        if (usuarioLogueado instanceof Paciente) {
            // Es un Paciente
            Paciente p = (Paciente) usuarioLogueado;
            this.currentUserId = (long) p.getId();
            this.currentUserType = "PACIENTE";
            this.esPaciente = true;
            // Un paciente solo puede chatear con empleados
            this.listaEmpleados = delegateMensaje.getTodosEmpleados();
            System.out.println("ChatBean init: Usuario es PACIENTE (ID: " + this.currentUserId + ")");

        } else if (usuarioLogueado instanceof Empleado) {
            // Es un Empleado/Dentista
            Empleado e = (Empleado) usuarioLogueado;
            this.currentUserId = (long) e.getId();
            this.currentUserType = "EMPLEADO";
            this.esEmpleado = true;
            // Un empleado solo puede chatear con pacientes
            this.listaPacientes = delegateMensaje.getTodosPacientes();
            System.out.println("ChatBean init: Usuario es EMPLEADO (ID: " + this.currentUserId + ")");

        } else if (usuarioLogueado instanceof Administrador) {
            // Es un Administrador
            Administrador a = (Administrador) usuarioLogueado;
            // El Admin funciona como un Empleado para chatear
            this.currentUserId = (long) a.getId();
            this.currentUserType = "EMPLEADO";
            this.esEmpleado = true; // Para mostrar la lista de pacientes
            this.listaPacientes = delegateMensaje.getTodosPacientes();
            System.out.println("ChatBean init: Usuario es ADMINISTRADOR (ID: " + this.currentUserId + ")");

        } else {
            // Si en daaaaado caso alguien llega al chat sin logearse
            System.err.println("ChatBean: No se pudo identificar al usuario logueado en la sesión.");
            // Redirigimos si en dado caso no se pudo encontrar el login
            // FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
        }
    }

    /**
     * Carga el historial de chat con esa persona.
     * @param id El ID del contacto (Paciente o Empleado)
     * @param tipo El Tipo del contacto (Paciente o Empleado)
     * @param nombre El Nombre completo para mostrar en la cabecera
     */
    public void seleccionarContacto(Long id, String tipo, String nombre) {
        this.contactoSeleccionadoId = id;
        this.contactoSeleccionadoTipo = tipo;
        this.contactoSeleccionadoNombre = nombre;

        System.out.println("Cargando chat entre " + currentUserType + ":" + currentUserId + " y " + tipo + ":" + id);

        // Marcar mensajes como leídos
        delegateMensaje.marcarComoLeidos(currentUserId, currentUserType, contactoSeleccionadoId, contactoSeleccionadoTipo);

        // Cargar el historial de chat
        this.chatHistory = delegateMensaje.getHistorialChat(
                currentUserId, currentUserType,
                contactoSeleccionadoId, contactoSeleccionadoTipo
        );
    }

    // Acción llamada por el botón "Enviar".
    public void enviarMensaje() {
        if (nuevoMensaje == null || nuevoMensaje.trim().isEmpty() || contactoSeleccionadoId == null) {
            return;
        }

        Mensaje msg = new Mensaje();
        msg.setContenido(nuevoMensaje.trim());
        msg.setIdEmisor(currentUserId);
        msg.setTipoEmisor(currentUserType);
        msg.setIdReceptor(contactoSeleccionadoId);
        msg.setTipoReceptor(contactoSeleccionadoTipo);

        boolean enviado = delegateMensaje.enviarMensaje(msg);

        if (enviado) {
            this.nuevoMensaje = "";
            this.chatHistory = delegateMensaje.getHistorialChat(
                    currentUserId, currentUserType,
                    contactoSeleccionadoId, contactoSeleccionadoTipo
            );
        } else {
            System.err.println("No se pudo enviar el mensaje.");
        }
    }

    // Getters y Setters

    public boolean isEsPaciente() {
        return esPaciente;
    }

    public boolean isEsEmpleado() {
        return esEmpleado;
    }

    public List<Paciente> getListaPacientes() { return listaPacientes; }
    public void setListaPacientes(List<Paciente> listaPacientes) { this.listaPacientes = listaPacientes; }
    public List<Empleado> getListaEmpleados() { return listaEmpleados; }
    public void setListaEmpleados(List<Empleado> listaEmpleados) { this.listaEmpleados = listaEmpleados; }
    public List<Mensaje> getChatHistory() { return chatHistory; }
    public void setChatHistory(List<Mensaje> chatHistory) { this.chatHistory = chatHistory; }
    public String getNuevoMensaje() { return nuevoMensaje; }
    public void setNuevoMensaje(String nuevoMensaje) { this.nuevoMensaje = nuevoMensaje; }
    public Long getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(Long currentUserId) { this.currentUserId = currentUserId; }
    public String getCurrentUserType() { return currentUserType; }
    public void setCurrentUserType(String currentUserType) { this.currentUserType = currentUserType; }
    public Long getContactoSeleccionadoId() { return contactoSeleccionadoId; }
    public void setContactoSeleccionadoId(Long contactoSeleccionadoId) { this.contactoSeleccionadoId = contactoSeleccionadoId; }
    public String getContactoSeleccionadoTipo() { return contactoSeleccionadoTipo; }
    public void setContactoSeleccionadoTipo(String tipoContactoSeleccionado) { this.contactoSeleccionadoTipo = tipoContactoSeleccionado; }
    public String getContactoSeleccionadoNombre() { return contactoSeleccionadoNombre; }
    public void setContactoSeleccionadoNombre(String contactoSeleccionadoNombre) { this.contactoSeleccionadoNombre = contactoSeleccionadoNombre; }
}