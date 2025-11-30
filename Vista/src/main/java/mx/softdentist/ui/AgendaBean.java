package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.delegate.DelegateCita;
import mx.softdentist.entidad.Cita;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Named("agendaBean")
@ViewScoped
public class AgendaBean implements Serializable {

    private ScheduleModel eventModel;
    private DelegateCita delegateCita;
    private ScheduleEvent<?> event = new DefaultScheduleEvent<>();

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        delegateCita = new DelegateCita();


        cargarEventos();
    }

    private void cargarEventos() {
        try {

            List<Cita> listaCitas = delegateCita.obtenerTodasCitas();

            if (listaCitas != null && !listaCitas.isEmpty()) {
                for (Cita c : listaCitas) {


                    if (c.getFecha() == null || c.getHora() == null) continue;

                    LocalDateTime inicio = c.getFecha().atTime(c.getHora());

                    LocalDateTime fin = inicio.plusHours(1);

                    String nombrePaciente = "Paciente Desconocido";
                    if (c.getIdPaciente() != null) {
                        nombrePaciente = c.getIdPaciente().getNombre() + " " + c.getIdPaciente().getApellido();
                    }
                    String titulo = nombrePaciente + " - " + (c.getMotivo() != null ? c.getMotivo() : "");

                    Cita.EstadoCita estado = c.getEstado();
                    String color = obtenerColorPorEstado(estado);

                    DefaultScheduleEvent<?> nuevoEvento = DefaultScheduleEvent.builder()
                            .title(titulo)
                            .startDate(inicio)
                            .endDate(fin)
                            .description(estado.name())
                            .borderColor(color)
                            .backgroundColor(color)
                            .data(c)
                            .build();

                    eventModel.addEvent(nuevoEvento);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar las citas de la BD."));
        }
    }

    private String obtenerColorPorEstado(Cita.EstadoCita estado) {
        if (estado == null) return "#3788d8";

        switch (estado) {
            case Pendiente:
                return "#f39c12";
            case Completada:
                return "#27ae60";
            case Cancelada:
                return "#c0392b";
            case Programada:
                return "#8e44ad";
            default:
                return "#3788d8";
        }
    }


    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        this.event = selectEvent.getObject();
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleEvent<?> getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent<?> event) {
        this.event = event;
    }
}