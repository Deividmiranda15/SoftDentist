package mx.softdentist.ui; // Asegúrate que el paquete sea el correcto

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem; // Importante: usa el de jakarta.faces.model
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date; // Importante: usa java.util.Date para el p:calendar
import java.util.List;
// NOTA: No necesitas 'java.time.LocalDate' en este bean si p:calendar usa 'java.util.Date'

@Named
@ViewScoped // Usamos ViewScoped para mantener el estado en la página
public class CitaBean implements Serializable {

    // --- Atributos para el formulario ---
    // Deben llamarse así para coincidir con el xhtml
    private Date fechaSeleccionada;
    private String horaSeleccionada;
    private String motivoSeleccionado;

    private List<SelectItem> horasDelDia; // Lista para los botones de hora

    // --- Lógica de negocio (aquí irían tus Facades/Delegates) ---
    // @Inject
    // private FacadeCita facadeCita; // Descomenta cuando conectes tu lógica de negocio

    @PostConstruct
    public void init() {
        // Inicializa la lista de horas
        horasDelDia = new ArrayList<>();
    }

    /**
     * Este método se dispara con el p:ajax del calendario.
     * Aquí es donde debes consultar la base de datos.
     */
    public void onDateSelect(SelectEvent<Date> event) {
        this.fechaSeleccionada = event.getObject();
        this.horaSeleccionada = null; // Resetea la hora seleccionada
        this.motivoSeleccionado = null; // Resetea el motivo

        cargarHorasDisponibles();
    }

    /**
     * SIMULACIÓN DE CONSULTA A BD
     * Este método debería usar tu FacadeCita para traer las citas
     * ya agendadas en 'fechaSeleccionada' y deshabilitar esos horarios.
     */
    private void cargarHorasDisponibles() {
        horasDelDia = new ArrayList<>();

        // 1. Simula las horas que ya están "ocupadas" en la BD para ese día
        // En un caso real: List<Cita> citasDelDia = facadeCita.consultarPorFecha(fechaSeleccionada);
        // List<String> horasOcupadas = citasDelDia.stream().map(Cita::getHora).collect(Collectors.toList());

        // Simulación: El 10:00 y 13:00 están ocupados
        List<String> horasOcupadas = Arrays.asList("10:00", "13:00");

        // 2. Define todos los horarios posibles del consultorio
        List<String> horariosClinica = Arrays.asList(
                "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "16:00", "17:00"
        );

        // 3. Crea los SelectItem, marcando como 'disabled' los que estén ocupados
        for (String hora : horariosClinica) {
            boolean deshabilitado = horasOcupadas.contains(hora);
            String label = formatHoraLabel(hora); // "09:00 AM"
            // El formato es: (value, label, description, disabled)
            horasDelDia.add(new SelectItem(hora, label, "", deshabilitado));
        }
    }

    // Método de ayuda para formatear la etiqueta
    private String formatHoraLabel(String hora) {
        try {
            int h = Integer.parseInt(hora.split(":")[0]);
            String ampm = (h < 12) ? "AM" : "PM";
            if (h > 12) h -= 12;
            return String.format("%02d:00 %s", h, ampm);
        } catch (Exception e) {
            return hora; // Fallback
        }
    }

    /**
     * Acción del botón "Solicitar Cita".
     */
    public void guardarCita() { // Se llama 'guardarCita' en el xhtml
        if (fechaSeleccionada == null || horaSeleccionada == null || motivoSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Campos incompletos", "Por favor, selecciona fecha, hora y motivo."));
            return;
        }

        try {
            // --- AQUÍ VA LA LÓGICA DE GUARDADO EN BD ---
            // Aquí usarías tu ServiceLocator que vi en tu bean original
            // Paciente p = ServiceLocator.getInstancePacienteDAO().find(pacienteId);
            // Cita nuevaCita = new Cita();
            // nuevaCita.setFecha( ... convertir java.util.Date a java.time.LocalDate ... );
            // nuevaCita.setHora(LocalTime.parse(horaSeleccionada));
            // nuevaCita.setMotivo(motivoSeleccionado);
            // nuevaCita.setIdPaciente(p);
            // ServiceLocator.getInstanceCitaDAO().save(nuevaCita);

            System.out.println("Guardando Cita:");
            System.out.println("Fecha: " + fechaSeleccionada);
            System.out.println("Hora: " + horaSeleccionada);
            System.out.println("Motivo: " + motivoSeleccionado);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Cita Agendada", "Tu cita ha sido registrada exitosamente."));

            // Limpiar formulario
            this.fechaSeleccionada = null;
            this.horaSeleccionada = null;
            this.motivoSeleccionado = null;
            this.horasDelDia.clear(); // Limpia la lista de horas

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar", "No se pudo registrar la cita: " + e.getMessage()));
        }
    }

    // --- Getters y Setters --- (Estos SÍ deben coincidir con el xhtml)

    /**
     * Devuelve la fecha de hoy para deshabilitar días anteriores en el calendario.
     */
    public Date getToday() {
        return new Date();
    }

    public Date getFechaSeleccionada() {
        return fechaSeleccionada;
    }

    public void setFechaSeleccionada(Date fechaSeleccionada) {
        this.fechaSeleccionada = fechaSeleccionada;
    }

    public String getHoraSeleccionada() {
        return horaSeleccionada;
    }

    public void setHoraSeleccionada(String horaSeleccionada) {
        this.horaSeleccionada = horaSeleccionada;
    }

    public String getMotivoSeleccionado() {
        return motivoSeleccionado;
    }

    public void setMotivoSeleccionado(String motivoSeleccionado) {
        this.motivoSeleccionado = motivoSeleccionado;
    }

    public List<SelectItem> getHorasDelDia() {
        return horasDelDia;
    }

    public void setHorasDelDia(List<SelectItem> horasDelDia) {
        this.horasDelDia = horasDelDia;
    }
}