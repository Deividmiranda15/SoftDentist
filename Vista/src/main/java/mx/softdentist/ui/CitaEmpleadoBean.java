package mx.softdentist.ui;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import jakarta.faces.application.FacesMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.event.SelectEvent;

@Named
@ViewScoped
public class CitaEmpleadoBean implements Serializable {

    // --- Variables para Datos ---
    private List<Cita> citas;
    private Cita citaSeleccionada;
    private ScheduleModel eventModel;
    private ScheduleEvent<?> event;

    // --- Variables para Lógica de UI ---
    private String vistaActual = "LISTA"; // Por defecto iniciamos en lista
    private String observaciones;
    private String estadoSeleccionado;
    private String observacionesReceta; // Para el dialog de receta

    @PostConstruct
    public void init() {
        cargarCitasConDetalles();
    }

    // --- Lógica de Navegación (Tabs) ---
    public void cambiarVista(String vista) {
        this.vistaActual = vista;
        // Si cambiamos a calendario, nos aseguramos que los eventos estén frescos
        if("CALENDARIO".equals(vista)) {
            cargarCalendario();
        }
    }

    private void cargarCitasConDetalles() {
        try {
            // Usamos el método existente en tu DAO
            citas = ServiceLocator.getInstanceCitaDAO().obtenerTodasConPacientes();
            // Al cargar la lista, cargamos también el calendario
            cargarCalendario();
        } catch (Exception e) {
            mostrarMensaje("Error al cargar citas: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            e.printStackTrace();
        }
    }

    // --- Lógica del Calendario ---
    private void cargarCalendario() {
        eventModel = new DefaultScheduleModel();

        if (citas != null) {
            for (Cita c : citas) {
                if (c.getFecha() != null && c.getHora() != null) {
                    // Combinar fecha y hora para el inicio
                    LocalDateTime fechaHoraInicio = LocalDateTime.of(c.getFecha(), c.getHora());
                    // Asumimos duración de 1 hora por defecto
                    LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(1);

                    // El título del evento será el nombre del paciente
                    String titulo = c.getIdPaciente().getNombre() + " " + c.getIdPaciente().getApellido();

                    // Color según estado (opcional)
                    String estilo = "fc-event-programada";
                    if("Completada".equals(c.getEstado().toString())) estilo = "fc-event-completada";
                    if("Cancelada".equals(c.getEstado().toString())) estilo = "fc-event-cancelada";

                    DefaultScheduleEvent<?> evt = DefaultScheduleEvent.builder()
                            .title(titulo)
                            .startDate(fechaHoraInicio)
                            .endDate(fechaHoraFin)
                            .description(c.getMotivo())
                            .data(c) // Guardamos el objeto Cita completo en el evento
                            .styleClass(estilo)
                            .build();

                    eventModel.addEvent(evt);
                }
            }
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        event = selectEvent.getObject();
        // Recuperamos la cita guardada en la data del evento
        citaSeleccionada = (Cita) event.getData();
    }

    // --- Lógica de Lista (Acciones) ---

    public void seleccionarCita(Cita c) {
        this.citaSeleccionada = c;
        // Pre-cargar estado actual para el combo
        if(c.getEstado() != null) {
            this.estadoSeleccionado = c.getEstado().toString();
        }
    }

    public void actualizarEstado() {
        try {
            if(citaSeleccionada != null && estadoSeleccionado != null) {
                citaSeleccionada.setEstado(Cita.EstadoCita.valueOf(estadoSeleccionado));
                ServiceLocator.getInstanceCitaDAO().update(citaSeleccionada);
                mostrarMensaje("Estado actualizado correctamente.", FacesMessage.SEVERITY_INFO);
                cargarCitasConDetalles(); // Recargar para ver cambios
            }
        } catch (Exception e) {
            mostrarMensaje("Error al actualizar: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void enviarReceta() {
        // Usamos la lógica de generarReceta que ya tenías, pero adaptada al dialog
        this.observaciones = this.observacionesReceta;
        generarReceta(); // Llama a tu método original de generación de PDF
    }

    // --- Tu método original de Generar Receta (Mantenido) ---
    public void generarReceta() {
        try {
            if (citaSeleccionada == null) {
                mostrarMensaje("Seleccione una cita.", FacesMessage.SEVERITY_WARN);
                return;
            }
            // ... (resto de tu lógica de PDF existente se mantiene igual) ...
            // Crear directorio si no existe
            FacesContext context = FacesContext.getCurrentInstance();
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            String recetaDir = servletContext.getRealPath("/resources/recetas/");

            File directorio = new File(recetaDir);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String nombreArchivo = "receta_cita" + citaSeleccionada.getId() + ".pdf";
            String rutaArchivo = recetaDir + File.separator + nombreArchivo;

            // Generar PDF
            generarPDF(rutaArchivo);

            // Actualizar estado a "Completada"
            // citaSeleccionada.setEstado(Cita.EstadoCita.Completada); // Descomentar si deseas cambio automático
            // ServiceLocator.getInstanceCitaDAO().update(citaSeleccionada);

            mostrarMensaje("Receta generada exitosamente.", FacesMessage.SEVERITY_INFO);

            // Limpiar
            observacionesReceta = "";

        } catch (Exception e) {
            mostrarMensaje("Error al generar receta: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            e.printStackTrace();
        }
    }

    private void generarPDF(String rutaArchivo) throws Exception {
        // ... (Tu lógica existente de iText se mantiene igual) ...
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
        document.open();
        document.add(new Paragraph("Receta Médica - SoftDentist"));
        document.add(new Paragraph("Paciente: " + citaSeleccionada.getIdPaciente().getNombre()));
        document.add(new Paragraph("Observaciones: " + observaciones));
        document.close();
    }

    private void mostrarMensaje(String mensaje, FacesMessage.Severity severidad) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, mensaje, null));
    }

    // --- Getters y Setters ---
    public List<Cita> getCitas() { return citas; }
    public Cita getCitaSeleccionada() { return citaSeleccionada; }
    public void setCitaSeleccionada(Cita citaSeleccionada) { this.citaSeleccionada = citaSeleccionada; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getVistaActual() { return vistaActual; }
    public ScheduleModel getEventModel() { return eventModel; }
    public ScheduleEvent<?> getEvent() { return event; }
    public void setEvent(ScheduleEvent<?> event) { this.event = event; }
    public String getEstadoSeleccionado() { return estadoSeleccionado; }
    public void setEstadoSeleccionado(String estadoSeleccionado) { this.estadoSeleccionado = estadoSeleccionado; }
    public String getObservacionesReceta() { return observacionesReceta; }
    public void setObservacionesReceta(String observacionesReceta) { this.observacionesReceta = observacionesReceta; }
}