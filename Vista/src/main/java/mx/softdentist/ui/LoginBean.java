package mx.softdentist.ui;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.softdentist.dao.AdministradorDAO;
import mx.softdentist.dao.EmpleadoDAO;
import mx.softdentist.dao.PacienteDAO;
import mx.softdentist.entidad.Administrador;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;

@Named("loginBean")
@RequestScoped
public class LoginBean {

    private String correo;
    private String password;

    private Paciente pacienteLogueado; // 🔹 Nuevo atributo para usar en cita.xhtml

    private transient AdministradorDAO administradorDAO;
    private transient EmpleadoDAO empleadoDAO;
    private transient PacienteDAO pacienteDAO;

    public LoginBean() {
        this.administradorDAO = ServiceLocator.getInstanceAdministradorDAO();
        this.empleadoDAO = ServiceLocator.getInstanceEmpleadoDAO();
        this.pacienteDAO = ServiceLocator.getInstancePacienteDAO();
    }

    public String iniciarSesion() {
        FacesContext context = FacesContext.getCurrentInstance();

        System.out.println("Intentando iniciar sesión con correo: " + correo);

        // --- Intento 1: Administrador ---
        try {
            Administrador admin = administradorDAO.findByCorreo(correo);
            if (admin != null) {
                if (admin.getPassword().equals(password)) {
                    context.getExternalContext().getSessionMap().put("usuarioLogueado", admin);
                    context.getExternalContext().getSessionMap().put("tipoUsuario", "administrador");
                    return "inicio_admin?faces-redirect=true";
                } else {
                    mostrarError("Correo o contraseña incorrectos.");
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error verificando Administrador: " + e.getMessage());
        }

        // --- Intento 2: Empleado ---
        try {
            Empleado empleado = empleadoDAO.findByCorreo(correo);
            if (empleado != null) {
                if (empleado.getPassword().equals(password)) {
                    context.getExternalContext().getSessionMap().put("usuarioLogueado", empleado);
                    context.getExternalContext().getSessionMap().put("tipoUsuario", "empleado");
                    return "inicio_emp?faces-redirect=true";
                } else {
                    mostrarError("Correo o contraseña incorrectos.");
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error verificando Empleado: " + e.getMessage());
        }

        // --- Intento 3: Paciente ---
        try {
            Paciente paciente = pacienteDAO.findByCorreo(correo);
            if (paciente != null) {
                if (paciente.getPassword().equals(password)) {
                    this.pacienteLogueado = paciente; // 🔹 Guardar para uso en cita.xhtml

                    // 🔹 Guardar también en sesión
                    context.getExternalContext().getSessionMap().put("usuarioLogueado", paciente);
                    context.getExternalContext().getSessionMap().put("tipoUsuario", "paciente");

                    return "inicio_paciente?faces-redirect=true";
                } else {
                    mostrarError("Correo o contraseña incorrectos.");
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error verificando Paciente: " + e.getMessage());
        }

        // --- Si no coincide con ninguno ---
        mostrarError("Correo o contraseña incorrectos.");
        return null;
    }

    private void mostrarError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    // --- Getters y Setters ---
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Paciente getPacienteLogueado() { return pacienteLogueado; }
    public void setPacienteLogueado(Paciente pacienteLogueado) { this.pacienteLogueado = pacienteLogueado; }
}
