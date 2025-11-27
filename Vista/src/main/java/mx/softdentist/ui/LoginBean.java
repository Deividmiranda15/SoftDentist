package mx.softdentist.ui;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
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

    private Paciente pacienteLogueado; // Nuevo atributo para usar en cita.xhtml

    private transient AdministradorDAO administradorDAO;
    private transient EmpleadoDAO empleadoDAO;
    private transient PacienteDAO pacienteDAO;

    @Inject
    private SessionBean sessionBean;

    public LoginBean() {
        this.administradorDAO = ServiceLocator.getInstanceAdministradorDAO();
        this.empleadoDAO = ServiceLocator.getInstanceEmpleadoDAO();
        this.pacienteDAO = ServiceLocator.getInstancePacienteDAO();
    }

    public String iniciarSesion() {
        System.out.println("--- INICIO DE SESIÓN INTENTADO ---");
        System.out.println("Correo ingresado: " + this.correo);

        // --- Intento 1: Iniciar sesión como Administrador ---
        try {
            System.out.println("1. Verificando como Administrador...");
            Administrador admin = administradorDAO.findByCorreo(this.correo);

            if (admin != null) { // Administrador SÍ fue encontrado
                System.out.println("Resultado: Administrador ENCONTRADO.");
                if (admin.getPassword().equals(this.password)) {
                    System.out.println("Resultado: Contraseña CORRECTA. Redirigiendo a inicio_admin...");

                    sessionBean.setUsuarioLogueado(admin);

                    return "inicio_admin?faces-redirect=true";
                } else {
                    System.out.println("Resultado: Contraseña INCORRECTA para Admin.");
                    // DETENERSE: Usuario encontrado, pero contraseña incorrecta.
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo o contraseña incorrectos."));
                    return null; // Detiene el proceso, no intentes como Empleado o Paciente
                }
            } else {
                System.out.println("Resultado: Administrador NO encontrado. Intentando como Empleado...");
            }
        } catch (Exception e) {
            System.err.println("Error verificando Administrador: " + e.getMessage());
        }

        // --- Intento 2: Empleado ---
        try {
            System.out.println("2. Verificando como Empleado...");
            Empleado empleado = empleadoDAO.findByCorreo(this.correo);

            if (empleado != null) { // Empleado SÍ fue encontrado
                System.out.println("Resultado: Empleado ENCONTRADO: " + empleado.getNombre());
                if (empleado.getPassword().equals(this.password)) {
                    System.out.println("Resultado: Contraseña CORRECTA. Redirigiendo a inicio_emp...");

                    sessionBean.setUsuarioLogueado(empleado);

                    return "inicio_emp?faces-redirect=true";
                } else {
                    System.out.println("Resultado: Contraseña INCORRECTA para Empleado.");
                    // DETENERSE: Usuario encontrado, pero contraseña incorrecta.
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo o contraseña incorrectos."));
                    return null; // Detiene el proceso
                }
            } else {
                System.out.println("Resultado: Empleado NO encontrado. Intentando como Paciente...");
            }
        } catch (Exception e) {
            System.err.println("Error verificando Empleado: " + e.getMessage());
            e.printStackTrace();
        }

        // --- Intento 3: Paciente ---
        try {
            System.out.println("3. Verificando como Paciente...");
            Paciente paciente = pacienteDAO.findByCorreo(this.correo);

            if (paciente != null) {
                System.out.println("Resultado: Paciente ENCONTRADO.");

                if (paciente.getPassword().equals(this.password)) {
                    System.out.println("Resultado: Contraseña CORRECTA. Redirigiendo a inicio_paciente...");

                    sessionBean.setUsuarioLogueado(paciente);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogueado", paciente);

                    return "inicio_paciente?faces-redirect=true";
                } else {
                    System.out.println("Resultado: Contraseña INCORRECTA para Paciente.");
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo o contraseña incorrectos."));
                    return null;
                }
            } else {
                System.out.println("Resultado: Paciente NO encontrado en la base de datos.");
            }
        } catch (Exception e) {
            System.err.println("Error verificando Paciente: " + e.getMessage());
            e.printStackTrace();
        }

        // --- Si todos los intentos fallan (ningún usuario encontrado con ese correo) ---
        System.out.println("--- FIN DE SESIÓN: FALLIDO (Correo no existe en ninguna tabla) ---");
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo o contraseña incorrectos."));
        return null; // Se queda en la misma página (login.xhtml)
    }

    // --- Getters y Setters ---
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Paciente getPacienteLogueado() { return pacienteLogueado; }
    public void setPacienteLogueado(Paciente pacienteLogueado) { this.pacienteLogueado = pacienteLogueado; }
}
