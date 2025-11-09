package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
// import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
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

    @Inject
    private SessionBean sessionBean;

    // Variables de estado
    private DelegateMensaje delegateMensaje;

    // Lista de contactos
    private List<Paciente> listaPacientes;
    private List<Empleado> listaEmpleados;

    // Estado del chat
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

        if (!sessionBean.isLogueado()) {
            System.err.println("ChatBean: ¡Nadie logueado! (Redirigir a login sería buena idea)");
            return;
        }

        // Object usuarioLogueado = FacesContext.getCurrentInstance()
        //        .getExternalContext().getSessionMap().get("usuarioLogueado");

        // Copiamos los datos del SessionBean a este bean
        this.currentUserId = sessionBean.getUsuarioId();
        this.currentUserType = sessionBean.getUsuarioTipo();
        this.esPaciente = sessionBean.isEsPaciente();
        this.esEmpleado = sessionBean.isEsEmpleado(); // Admin también es empleado, pero al final no voy a usar a admin para los chats

        // Cargar las listas de contactos correctas
        if (this.esPaciente) {
            // Un paciente solo puede chatear con empleados
            this.listaEmpleados = delegateMensaje.getTodosEmpleados();
            System.out.println("ChatBean init: PACIENTE (ID: " + this.currentUserId + ") cargando empleados.");

        } else if (this.esEmpleado) {
            // Un empleado solo puede chatear con pacientes
            this.listaPacientes = delegateMensaje.getTodosPacientes();
            System.out.println("ChatBean init: EMPLEADO/ADMIN (ID: " + this.currentUserId + ") cargando pacientes.");
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

    // Acción llamada por el botón Enviar
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