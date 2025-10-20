package mx.softdentist.ui;

import jakarta.faces.view.ViewScoped;
import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class CitaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private String hora;
    private String motivo;
    private Paciente paciente; // Asegúrate de asignar el paciente actual
    private List<String> horasDisponibles;
    private List<LocalDate> fechasDisponibles;
    private List<Cita> citasRegistradas;
    private List<Paciente> pacientesDisponibles; // Lista de pacientes
    private Integer pacienteSeleccionadoId;      // ID del paciente seleccionado

    public CitaBean() {
        cargarFechasDisponibles();
        cargarCitasRegistradas();
    }

    public void solicitarCita() {
        try {
            // Validaciones básicas de fecha, hora y motivo
            if (!validarCita()) {
                String mensajeError = obtenerMensajeError();
                showToastMessage("error", "Error de Validación", mensajeError);
                return;
            }

            // Validar que se haya seleccionado un paciente
            if (pacienteSeleccionadoId == null) {
                showToastMessage("error", "Error de Validación", "Debe seleccionar un paciente.");
                return;
            }

            // Obtener el objeto Paciente desde el ID seleccionado
            Paciente pacienteSeleccionado = ServiceLocator.getInstancePacienteDAO()
                    .find(pacienteSeleccionadoId)
                    .orElse(null);

            if (pacienteSeleccionado == null) {
                showToastMessage("error", "Error de Validación", "Paciente no válido.");
                return;
            }

            // Crear la nueva cita
            Cita cita = new Cita();
            cita.setFecha(fecha);
            cita.setHora(LocalTime.parse(hora)); // Convierte String a LocalTime
            cita.setMotivo(motivo);
            cita.setEstado("Pendiente");
            cita.setIdPaciente(pacienteSeleccionado); // Asignar el paciente seleccionado

            // Guardar la cita usando el DAO
            ServiceLocator.getInstanceCitaDAO().save(cita);

            showToastMessage("success", "¡Cita Solicitada!",
                    "Su cita para el " + fecha + " a las " + hora + " ha sido solicitada. Estado: Pendiente");

            // Limpiar formulario
            limpiarFormulario();

        } catch (Exception e) {
            showToastMessage("error", "Error del Sistema",
                    "Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCita() {
        if (fecha == null) return false;
        if (hora == null) return false;
        if (motivo == null || motivo.trim().isEmpty()) return false;
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) return false;
        if (fecha.isBefore(LocalDate.now())) return false;
        return true;
    }

    private String obtenerMensajeError() {
        if (fecha == null) return "Debe seleccionar una fecha.";
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) return "No se pueden agendar citas los domingos.";
        if (fecha.isBefore(LocalDate.now())) return "No se pueden agendar citas en fechas pasadas.";
        if (hora == null) return "Debe seleccionar una hora.";
        if (motivo == null || motivo.trim().isEmpty()) return "Debe indicar un motivo.";
        return "Complete todos los campos correctamente.";
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

    public void onDateSelect() {
        cargarHorasDisponibles();
        if (fecha != null) {
            String mensaje = fecha.getDayOfWeek() == DayOfWeek.SATURDAY ?
                    "Horario de sábado: 10:00 - 14:00" :
                    "Horario: 10:00 - 20:00";
            showToastMessage("info", "Horario", mensaje);
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

    private void showToastMessage(String severity, String summary, String detail) {
        PrimeFaces.current().executeScript(
                "PrimeFaces.toast.show({severity: '" + severity + "', summary: '" + summary +
                        "', detail: '" + detail + "', life: 5000});"
        );
    }

    private void limpiarFormulario() {
        fecha = null;
        hora = null;
        motivo = null;
        horasDisponibles = null;
    }

    private void cargarCitasRegistradas() {
        citasRegistradas = ServiceLocator.getInstanceCitaDAO().obtenerTodos();
    }

    public List<Paciente> getPacientesDisponibles() {
        if (pacientesDisponibles == null) {
            pacientesDisponibles = ServiceLocator.getInstancePacienteDAO().findAll();
        }
        return pacientesDisponibles;
    }

    public Integer getPacienteSeleccionadoId() {
        return pacienteSeleccionadoId;
    }

    public void setPacienteSeleccionadoId(Integer pacienteSeleccionadoId) {
        this.pacienteSeleccionadoId = pacienteSeleccionadoId;
    }


    // Getters y setters
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
}
