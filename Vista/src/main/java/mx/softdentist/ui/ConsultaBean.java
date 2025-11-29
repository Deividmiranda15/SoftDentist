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



    @PostConstruct
    public void init() {
        delegateCita = new DelegateCita();
        listaCitas = delegateCita.obtenerTodasCitas();
        emailService = EmailService.getInstance();
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
    }
    
    public void cambiarEstado(Cita cita) {
        if (cita == null) return;

        switch (cita.getEstado()) {
            case Pendiente-> cita.setEstado(Cita.EstadoCita.Programada);
            case Programada -> cita.setEstado(Cita.EstadoCita.Completada);
            case Completada -> cita.setEstado(Cita.EstadoCita.Cancelada);
            case Cancelada -> cita.setEstado(Cita.EstadoCita.Pendiente);
            default -> cita.setEstado(Cita.EstadoCita.Pendiente);
        }

        delegateCita.actualizarCita(cita); // Llama a tu delegate para persistir cambios

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Estado actualizado",
                        "Cita de " + cita.getIdPaciente().getNombre() + " ahora está " + cita.getEstado()));
    }

    public void enviarReceta(Cita cita) {
        if (cita == null || cita.getIdPaciente() == null) return;

        String destinatario = cita.getIdPaciente().getCorreo();
        String asunto = "Receta Médica - Dental Patron";
        String cuerpo = "Hola " + cita.getIdPaciente().getNombre() + ",<br><br>" +
                "Adjunto tu receta correspondiente a la cita del " + cita.getFecha() + ".<br><br>" +
                "Saludos,<br>Dental Patron";

        emailService.enviarCorreoIndividual(destinatario, asunto, cuerpo);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Correo enviado",
                        "Receta enviada a " + cita.getIdPaciente().getNombre()));
    }

    public void actualizarEstado() {
        if (citaSeleccionada == null) return;

        citaSeleccionada.setEstado(Cita.EstadoCita.valueOf(estadoSeleccionado));
        delegateCita.actualizarCita(citaSeleccionada);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Estado actualizado", "Nuevo estado: " + estadoSeleccionado));
    }
}
