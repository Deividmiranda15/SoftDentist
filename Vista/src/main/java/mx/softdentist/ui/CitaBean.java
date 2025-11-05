package mx.softdentist.ui;

import jakarta.faces.view.ViewScoped;
import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;
import java.util.Date;

@Named
@ViewScoped
public class CitaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private String hora;
    private String motivo;
    private Paciente paciente;
    private List<String> horasDisponibles;
    private List<LocalDate> fechasDisponibles;
    private List<Cita> citasRegistradas;
    private List<Paciente> pacientesDisponibles;
    private Integer pacienteSeleccionadoId;
    private Cita citaSeleccionada;

    public CitaBean() {
        cargarFechasDisponibles();
        cargarCitasRegistradas();
    }

    /** MÉTODO PRINCIPAL: SOLICITAR CITA **/
    public void solicitarCita() {
        try {
            // 🧩 Validaciones de campos obligatorios
            if (pacienteSeleccionadoId == null) {
                showToastMessage("error", "Error de Validación", "Debe seleccionar un paciente.");
                return;
            }

            if (fecha == null) {
                showToastMessage("error", "Error de Validación", "Debe seleccionar una fecha.");
                return;
            }

            // 🚫 No se puede agendar el mismo día ni días anteriores
            if (!fecha.isAfter(LocalDate.now())) {
                showToastMessage("error", "Fecha inválida", "No puede agendar citas el mismo día o en días pasados.");
                return;
            }

            // 🚫 No se puede agendar los domingos
            if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
                showToastMessage("error", "Horario no disponible", "No se pueden agendar citas los domingos.");
                return;
            }

            // ⏰ Validar hora
            if (hora == null || hora.isEmpty()) {
                showToastMessage("error", "Error de Validación", "Debe seleccionar una hora.");
                return;
            }

            // 💬 Validar motivo
            if (motivo == null || motivo.trim().isEmpty()) {
                showToastMessage("error", "Error de Validación", "Debe indicar el motivo de la cita.");
                return;
            }

            // 🔍 Buscar paciente
            Paciente pacienteSeleccionado = ServiceLocator.getInstancePacienteDAO()
                    .find(pacienteSeleccionadoId)
                    .orElse(null);

            if (pacienteSeleccionado == null) {
                showToastMessage("error", "Error", "El paciente seleccionado no es válido.");
                return;
            }

            // 🗓️ Crear cita
            Cita cita = new Cita();
            cita.setFecha(fecha);
            cita.setHora(LocalTime.parse(hora));
            cita.setMotivo(motivo);
            cita.setEstado("Pendiente");
            cita.setIdPaciente(pacienteSeleccionado);

            // 💾 Guardar cita
            ServiceLocator.getInstanceCitaDAO().save(cita);

            // 🎉 Mensaje de éxito
            showToastMessage("success", "¡Cita solicitada con éxito!",
                    "Su cita para el " + fecha + " a las " + hora + " ha sido registrada correctamente.");

            // 🔄 Limpiar formulario
            limpiarFormulario();

            // 🔁 Actualizar lista de citas
            cargarCitasRegistradas();

        } catch (Exception e) {
            showToastMessage("error", "Error del Sistema",
                    "Ocurrió un error al solicitar la cita: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** APOYO Y VALIDACIONES **/
    public void onDateSelect() {
        cargarHorasDisponibles();
        if (fecha != null) {
            String mensaje = fecha.getDayOfWeek() == DayOfWeek.SATURDAY ?
                    "Horario de sábado: 10:00 - 14:00" :
                    "Horario: 10:00 - 20:00";
            showToastMessage("info", "Horario", mensaje);
        }
    }

    private void cargarFechasDisponibles() {
        fechasDisponibles = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (int i = 1; i <= 30; i++) {
            LocalDate f = hoy.plusDays(i);
            if (f.getDayOfWeek() != DayOfWeek.SUNDAY) {
                fechasDisponibles.add(f);
            }
        }
    }

    private void cargarHorasDisponibles() {
        horasDisponibles = new ArrayList<>();
        if (fecha == null) return;

        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {
            String[] sabado = {"10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30"};
            for (String h : sabado) horasDisponibles.add(h);
        } else {
            for (int h = 10; h < 20; h++) {
                horasDisponibles.add(h + ":00");
                horasDisponibles.add(h + ":30");
            }
        }
    }

    /** 🔔 Mostrar mensajes tipo Toast **/
    private void showToastMessage(String severity, String summary, String detail) {
        PrimeFaces.current().executeScript(
                "PrimeFaces.toast.show({severity: '" + severity + "', summary: '" + summary +
                        "', detail: '" + detail + "', life: 5000});"
        );
    }

    /** 🔁 Limpiar formulario **/
    private void limpiarFormulario() {
        fecha = null;
        hora = null;
        motivo = null;
        pacienteSeleccionadoId = null;
        horasDisponibles = null;
    }

    /** 📋 Cargar citas **/
    private void cargarCitasRegistradas() {
        citasRegistradas = ServiceLocator.getInstanceCitaDAO().obtenerTodos();
        if (citasRegistradas != null) {
            citasRegistradas = citasRegistradas.stream()
                    .filter(c -> !"Cancelada".equalsIgnoreCase(c.getEstado()))
                    .toList();
        }
    }

    /** ❌ Cancelar cita **/
    public void cancelarCita() {
        try {
            if (citaSeleccionada == null || citaSeleccionada.getId() == null) {
                showToastMessage("warn", "Aviso", "Debe seleccionar una cita para cancelar.");
                return;
            }

            if ("Cancelada".equalsIgnoreCase(citaSeleccionada.getEstado())) {
                showToastMessage("info", "Cita ya cancelada", "Esta cita ya fue cancelada anteriormente.");
                return;
            }

            ServiceLocator.getInstanceCitaDAO().cancelarCita(citaSeleccionada.getId());
            showToastMessage("success", "Cita Cancelada",
                    "La cita del " + citaSeleccionada.getFecha() + " a las " + citaSeleccionada.getHora() + " ha sido cancelada.");
            cargarCitasRegistradas();
        } catch (Exception e) {
            showToastMessage("error", "Error al cancelar", e.getMessage());
            e.printStackTrace();
        }
    }

    /** GETTERS Y SETTERS **/
    public Integer getPacienteSeleccionadoId() { return pacienteSeleccionadoId; }
    public void setPacienteSeleccionadoId(Integer pacienteSeleccionadoId) { this.pacienteSeleccionadoId = pacienteSeleccionadoId; }

    public Cita getCitaSeleccionada() { return citaSeleccionada; }
    public void setCitaSeleccionada(Cita citaSeleccionada) { this.citaSeleccionada = citaSeleccionada; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
        if (fecha != null) cargarHorasDisponibles();
    }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public List<String> getHorasDisponibles() { return horasDisponibles; }
    public List<LocalDate> getFechasDisponibles() { return fechasDisponibles; }
    public List<Cita> getCitasRegistradas() { return citasRegistradas; }

    public List<Paciente> getPacientesDisponibles() {
        if (pacientesDisponibles == null) {
            pacientesDisponibles = ServiceLocator.getInstancePacienteDAO().obtenerTodos();
        }
        return pacientesDisponibles;
    }

    public void setPacientesDisponibles(List<Paciente> pacientesDisponibles) {
        this.pacientesDisponibles = pacientesDisponibles;
    }

    public Date getManana() {
        LocalDate manana = LocalDate.now().plusDays(1);
        return Date.from(manana.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
