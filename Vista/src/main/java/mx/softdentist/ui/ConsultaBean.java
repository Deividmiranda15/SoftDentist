package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.delegate.DelegateCita;
import mx.softdentist.entidad.Cita;
import mx.softdentist.util.EmailService;

import java.io.Serializable;
import java.util.List;

@Named("citaConsultaBean")
@ViewScoped

public class ConsultaBean implements Serializable {

    private List<Cita> listaCitas;
    private DelegateCita delegateCita;
    private EmailService emailService;
    private String estadoSeleccionado;
    private Cita citaSeleccionada;
    private String observacionesReceta;


    @PostConstruct
    public void init() {
        delegateCita = new DelegateCita();
        emailService = EmailService.getInstance();
        this.listaCitas = delegateCita.obtenerTodasCitas();
    }

    public List<Cita> getListaCitas() {
        return listaCitas;
    }

    public String getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    public void setEstadoSeleccionado(String estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    public void seleccionarCita(Cita c) {
        this.citaSeleccionada = c;
        this.estadoSeleccionado = c.getEstado().name();
        this.observacionesReceta = "";
    }

    public void enviarReceta() {
        if (citaSeleccionada == null || citaSeleccionada.getIdPaciente() == null)
            return;

        String destinatario = citaSeleccionada.getIdPaciente().getCorreo();
        String asunto = "Receta Médica - Dental Patron";

        String cuerpo =
                "Hola " + citaSeleccionada.getIdPaciente().getNombre() + ",<br><br>" +
                        "Aquí está tu receta correspondiente a la cita del " + citaSeleccionada.getFecha() + ".<br><br>" +
                        "<b>Observaciones:</b><br>" +
                        observacionesReceta.replace("\n", "<br>") +
                        "<br><br>Saludos,<br>Dental Patron";

        emailService.enviarCorreoIndividual(destinatario, asunto, cuerpo);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Correo enviado",
                        "La receta fue enviada correctamente."));
    }

    public void actualizarEstado() {
        if (citaSeleccionada == null) return;

        citaSeleccionada.setEstado(Cita.EstadoCita.valueOf(estadoSeleccionado));
        delegateCita.actualizarCita(citaSeleccionada);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Estado actualizado", "Nuevo estado: " + estadoSeleccionado));
    }
    public String getObservacionesReceta() { return observacionesReceta; }
    public void setObservacionesReceta(String observacionesReceta) { this.observacionesReceta = observacionesReceta; }
}
