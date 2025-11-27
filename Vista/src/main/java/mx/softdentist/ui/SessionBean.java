package mx.softdentist.ui;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import mx.softdentist.entidad.Administrador;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.entidad.Paciente;

@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {

    private Object usuarioLogueado;
    private Long usuarioId;
    private String usuarioTipo; // "PACIENTE", "EMPLEADO", "ADMIN"

    // Banderas de roles para la UI
    private boolean esPaciente;
    private boolean esEmpleado;
    private boolean esAdmin;

    public SessionBean() {
        System.out.println("SessionBean Creado!");
    }

    /**
     * Llamado por el LoginBean para establecer el usuario actual.
     * Inspecciona el objeto y establece las banderas de rol.
     */
    public void setUsuarioLogueado(Object usuario) {
        this.usuarioLogueado = usuario;

        // Reseteamos todas las banderas
        this.esPaciente = false;
        this.esEmpleado = false;
        this.esAdmin = false;
        this.usuarioTipo = null;
        this.usuarioId = null;

        if (usuario instanceof Paciente) {
            Paciente p = (Paciente) usuario;
            this.esPaciente = true;
            this.usuarioTipo = "PACIENTE";
            this.usuarioId = (long) p.getId();
            System.out.println("SessionBean: Usuario es PACIENTE (ID: " + this.usuarioId + ")");

        } else if (usuario instanceof Empleado) {
            Empleado e = (Empleado) usuario;
            this.esEmpleado = true; // Un empleado normal
            this.usuarioTipo = "EMPLEADO";
            this.usuarioId = (long) e.getId();
            System.out.println("SessionBean: Usuario es EMPLEADO (ID: " + this.usuarioId + ")");

        } else if (usuario instanceof Administrador) {
            Administrador a = (Administrador) usuario;
            this.esAdmin = true;
            this.esEmpleado = true; // ¡Importante! Un Admin es TAMBIÉN un Empleado (para el chat)
            this.usuarioTipo = "EMPLEADO"; // El Admin chatea como un Empleado
            this.usuarioId = (long) a.getId();
            System.out.println("SessionBean: Usuario es ADMINISTRADOR (ID: " + this.usuarioId + ")");
        }
    }

    /**
     * Cierra la sesión y redirige al login.
     * @return La página de login.
     */
    public String cerrarSesion() {
        System.out.println("Cerrando sesión...");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.usuarioLogueado = null;
        this.esPaciente = false;
        this.esEmpleado = false;
        this.esAdmin = false;
        this.usuarioId = null;
        this.usuarioTipo = null;
        return "login?faces-redirect=true";
    }

    // Getters para la UI y otros Beans

    public Object getUsuarioLogueado() { return usuarioLogueado; }
    public Long getUsuarioId() { return usuarioId; }
    public String getUsuarioTipo() { return usuarioTipo; }

    public boolean isEsPaciente() { return esPaciente; }
    public boolean isEsEmpleado() { return esEmpleado; }
    public boolean isEsAdmin() { return esAdmin; }

    // Getter para saber si el usuario está logueado
    public boolean isLogueado() {
        return this.usuarioLogueado != null;
    }
}