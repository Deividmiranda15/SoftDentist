package ui;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.softdentist.dao.AdministradorDAO;
import mx.softdentist.dao.EmpleadoDAO;
import mx.softdentist.entidad.Administrador;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.integration.ServiceLocator;

@Named("loginBean")
@RequestScoped
public class LoginBean {

    private String correo;
    private String contrasena;

    private transient AdministradorDAO administradorDAO;
    private transient EmpleadoDAO empleadoDAO;

    public LoginBean() {
        this.administradorDAO = ServiceLocator.getInstanceAdministradorDAO();
        this.empleadoDAO = ServiceLocator.getInstanceEmpleadoDAO();
    }

    public String iniciarSesion() {
        System.out.println("--- INICIO DE SESIÓN INTENTADO ---");
        System.out.println("Correo ingresado: " + this.correo);

        // --- Intento 1: Iniciar sesión como Administrador ---
        try {
            System.out.println("1. Verificando como Administrador...");
            Administrador admin = administradorDAO.findByCorreo(this.correo);
            if (admin != null && admin.getContrasena().equals(this.contrasena)) {
                System.out.println("Resultado: ÉXITO como Administrador. Redirigiendo...");
                return "inicio_admin?faces-redirect=true";
            }
        } catch (Exception e) {
            System.out.println("ERROR al verificar como Administrador: " + e.getMessage());
        }

        // --- Intento 2: Iniciar sesión como Empleado ---
        try {
            System.out.println("2. Verificando como Empleado...");
            Empleado empleado = empleadoDAO.findByCorreo(this.correo);

            if (empleado == null) {
                System.out.println("Resultado: Empleado NO encontrado en la base de datos.");
            } else {
                System.out.println("Resultado: Empleado ENCONTRADO: " + empleado.getNombre());
                if (empleado.getContrasena().equals(this.contrasena)) {
                    System.out.println("Resultado: Contraseña CORRECTA. Redirigiendo a inicio_emp...");
                    return "inicio_emp?faces-redirect=true";
                } else {
                    System.out.println("Resultado: Contraseña INCORRECTA.");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR al verificar como Empleado: " + e.getMessage());
            e.printStackTrace(); // Esto imprimirá el error completo y detallado
        }

        // --- Si ambos intentos fallan ---
        System.out.println("--- FIN DE SESIÓN: FALLIDO ---");
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo o contraseña incorrectos."));
        return null; // Se queda en la misma página (login.xhtml)
    }

    // --- Getters y Setters ---
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}

