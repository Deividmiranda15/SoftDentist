package ui;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.softdentist.dao.AdministradorDAO;
import mx.softdentist.entidad.Administrador;
import mx.softdentist.integration.ServiceLocator;

@Named("loginBean")
@RequestScoped
public class LoginBean {

    private String correo;
    private String contrasena;
    private transient AdministradorDAO administradorDAO;

    public LoginBean() {
        // Asumo que tu ServiceLocator tiene un método para obtener el AdministradorDAO
        this.administradorDAO = ServiceLocator.getInstanceAdministradorDAO();
    }

    public String iniciarSesion() {
        try {
            // Llamamos al método que busca por correo
            Administrador admin = administradorDAO.findByCorreo(this.correo);

            // Verificación simple de la contraseña
            if (admin != null && admin.getContrasena().equals(this.contrasena)) {
                // Si es correcto, JSF usará la regla de navegación "inicio_admin"
                return "inicio_admin";
            } else {
                // Si es incorrecto, muestra un mensaje de error
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo o contraseña incorrectos."));
                return null; // Esto hace que se quede en la misma página (login.xhtml)
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error del sistema", "No se pudo procesar el inicio de sesión."));
            e.printStackTrace();
            return null;
        }
    }

    // --- Getters y Setters ---
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}

