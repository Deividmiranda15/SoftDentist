package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import mx.softdentist.delegate.DelegatePaciente;
import mx.softdentist.util.EmailService;

/**
 * Controlador para la gestión de anuncios masivos a pacientes por correo
 */
@Named("anuncioBean")
@ViewScoped
public class AnuncioBean implements Serializable {

    private String asunto;
    private String mensaje;

    // Para mostrar en la pantalla a cuántas personas les llegará
    private int totalDestinatarios;

    private DelegatePaciente delegatePaciente;

    @PostConstruct
    public void init() {
        delegatePaciente = new DelegatePaciente();
        cargarEstadisticas();
    }

    public void cargarEstadisticas() {
        try {
            List<String> correos = delegatePaciente.obtenerListaCorreos();
            this.totalDestinatarios = correos.size();
        } catch (Exception e) {
            this.totalDestinatarios = 0;
            System.err.println("Error cargando correos: " + e.getMessage());
        }
    }

    public void enviarAnuncio() {
        // Validaciones
        if (asunto == null || asunto.trim().isEmpty() || mensaje == null || mensaje.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debes escribir un asunto y un mensaje."));
            return;
        }

        try {
            // Obtener la lista fresca de correos
            List<String> destinatarios = delegatePaciente.obtenerListaCorreos();

            if (destinatarios.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "No hay pacientes con correo registrado."));
                return;
            }

            // Agregar prefijo en el asunto
            String asuntoOficial = "[ANUNCIO: Dental Patron] " + this.asunto;

            // Usar el EmailService para enviar el anuncio masivamente
            EmailService.getInstance().enviarAnuncioMasivo(destinatarios, asuntoOficial, this.mensaje);

            // Notificar éxito y limpiar
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Enviado!",
                            "El anuncio se está enviando a " + destinatarios.size() + " pacientes."));

            this.asunto = "";
            this.mensaje = "";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Hubo un problema al iniciar el envío."));
            e.printStackTrace();
        }
    }

    // Getters y Setters
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public int getTotalDestinatarios() { return totalDestinatarios; }
}