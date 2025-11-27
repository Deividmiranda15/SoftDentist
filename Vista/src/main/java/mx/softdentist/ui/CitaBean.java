package mx.softdentist.ui;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.dao.CitaDAO;
import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import org.primefaces.PrimeFaces;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.dao.EmpleadoDAO;
import org.primefaces.event.SelectEvent;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Named("citaBean")
@ViewScoped
public class CitaBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    private LocalDate fecha;
    private String hora;
    private String motivo;

    private List<String> horasDisponibles = new ArrayList<>();
    private List<LocalDate> fechasDisponibles;
    private List<Cita> citasRegistradas;
    private Cita citaSeleccionada;
    private Paciente pacienteObjetivo;
    private Empleado dentistaSeleccionado;
    private List<Empleado> listaDentistas;

    private CitaDAO citaDAO;

    public CitaBean() {
        this.citaDAO = ServiceLocator.getInstanceCitaDAO();
        cargarFechasDisponibles();
        cargarCitasRegistradas();
    }

    public void cargarDatosParaAgendar() {
        try {
            EmpleadoDAO empleadoDAO = ServiceLocator.getInstanceEmpleadoDAO();
            List<Empleado> todos = empleadoDAO.obtenerTodos();

            this.listaDentistas = todos.stream()
                    .filter(e -> "Dentista".equalsIgnoreCase(e.getPuesto()))
                    .collect(Collectors.toList());

            if (fecha == null) {
                fecha = LocalDate.now().plusDays(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage("error", "Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    public void agendarCitaPorAdmin() {
        try {
            if (pacienteObjetivo == null) {
                showToastMessage("error", "Error", "No hay paciente seleccionado.");
                return;
            }
            if (dentistaSeleccionado == null) {
                showToastMessage("error", "Error", "Debe asignar un dentista.");
                return;
            }
            if (fecha == null) {
                showToastMessage("error", "Error", "Seleccione una fecha.");
                return;
            }
            if (hora == null || hora.isEmpty()) {
                showToastMessage("error", "Error", "Seleccione una hora.");
                return;
            }

            // Crear la cita
            Cita nuevaCita = new Cita();
            nuevaCita.setFecha(fecha);
            nuevaCita.setHora(LocalTime.parse(hora));
            nuevaCita.setMotivo(motivo);
            nuevaCita.setEstado(Cita.EstadoCita.valueOf("Programada"));
            nuevaCita.setIdPaciente(pacienteObjetivo);
            nuevaCita.setIdEmpleado(dentistaSeleccionado);

            // Guardar en base de datos
            ServiceLocator.getInstanceCitaDAO().save(nuevaCita);

            showToastMessage("success", "Éxito", "Cita agendada correctamente.");
            limpiarFormulario();

        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage("error", "Error", "No se pudo agendar la cita: " + e.getMessage());
        }
    }

    public void solicitarCita() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            if (pacienteObjetivo == null) {
                showToastMessage("error", "Error de sesión",
                        "No se encontró la sesión del paciente. Inicie sesión nuevamente.");
                return;
            }

            if (fecha == null) {
                showToastMessage("error", "Fecha requerida",
                        "Debe seleccionar una fecha.");
                return;
            }

            if (!fecha.isAfter(LocalDate.now())) {
                showToastMessage("error", "Fecha no permitida",
                        "No puede agendar citas hoy ni en días anteriores.");
                return;
            }

            if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
                showToastMessage("error", "Consultorio cerrado",
                        "No se pueden registrar citas en domingo.");
                return;
            }

            if (hora == null || hora.isEmpty()) {
                showToastMessage("error", "Hora requerida",
                        "Debe seleccionar una hora.");
                return;
            }

            if (motivo == null || motivo.trim().isEmpty()) {
                showToastMessage("error", "Motivo requerido",
                        "Debe especificar el motivo de la cita.");
                return;
            }

            List<LocalTime> ocupadas = citaDAO.obtenerHorasOcupadas(fecha);
            if (ocupadas.contains(LocalTime.parse(hora))) {
                showToastMessage("warn", "Hora no disponible",
                        "La hora seleccionada ya fue tomada, elija otra por favor.");
                cargarHorasDisponibles();
                return;
            }

            Cita cita = new Cita();
            cita.setFecha(fecha);
            cita.setHora(LocalTime.parse(hora));
            cita.setMotivo(motivo);
            cita.setEstado(Cita.EstadoCita.Pendiente);
            cita.setIdPaciente(pacienteObjetivo);

            citaDAO.save(cita);

            showToastMessage("success", "Cita registrada",
                    "Su cita para el " + fecha + " a las " + hora + " fue registrada correctamente.");

            limpiarFormulario();
            cargarCitasRegistradas();

        } catch (Exception e) {
            showToastMessage("error", "Error del sistema",
                    "Ocurrió un error al registrar la cita: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void onDateSelect(SelectEvent<LocalDate> event) {

        this.fecha = event.getObject();

        if (fecha == null) {
            horasDisponibles = new ArrayList<>();
            return;
        }

        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            horasDisponibles = new ArrayList<>();
            showToastMessage("warn", "Consultorio cerrado",
                    "Los domingos no se atienden citas.");
            return;
        }

        if (!fecha.isAfter(LocalDate.now())) {
            horasDisponibles = new ArrayList<>();
            showToastMessage("error", "Fecha inválida",
                    "Solo puede agendar citas desde mañana en adelante.");
            return;
        }

        cargarHorasDisponibles();
    }



    private List<String> generarHorasDisponibles(LocalDate fecha) {
        List<String> generadas = new ArrayList<>();
        if (fecha == null) return generadas;

        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {
            String[] sabado = {"10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30"};
            generadas.addAll(Arrays.asList(sabado));
        } else {
            for (int h = 10; h < 20; h++) {
                generadas.add(String.format("%02d:00", h));
                generadas.add(String.format("%02d:30", h));
            }
        }
        return generadas;
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

    private List<LocalTime> generarHorasDisponiblesLocalTime(LocalDate fecha) {
        List<LocalTime> horas = new ArrayList<>();

        if (fecha == null) return horas;

        DayOfWeek dia = fecha.getDayOfWeek();

        if (dia == DayOfWeek.SUNDAY) {
            return horas;
        }

        LocalTime inicio;
        LocalTime fin;

        if (dia == DayOfWeek.SATURDAY) {
            inicio = LocalTime.of(10, 0);
            fin = LocalTime.of(14, 0);
        } else {
            inicio = LocalTime.of(10, 0);
            fin = LocalTime.of(20, 0);
        }

        LocalTime actual = inicio;
        while (!actual.isAfter(fin.minusMinutes(30))) {
            horas.add(actual);
            actual = actual.plusMinutes(30);
        }

        return horas;
    }


    private void cargarHorasDisponibles() {

        if (fecha == null) {
            horasDisponibles = new ArrayList<>();
            return;
        }

        if (!fecha.isAfter(LocalDate.now())) {
            horasDisponibles = new ArrayList<>();
            showToastMessage("error", "Fecha no válida",
                    "Solo puede seleccionar fechas posteriores a hoy.");
            return;
        }

        List<LocalTime> generadas = generarHorasDisponiblesLocalTime(fecha);
        List<LocalTime> ocupadas = citaDAO.obtenerHorasOcupadas(fecha);

        horasDisponibles = generadas.stream()
                .filter(h -> !ocupadas.contains(h))
                .map(h -> h.format(DateTimeFormatter.ofPattern("HH:mm")))
                .toList();

        if (horasDisponibles.isEmpty()) {
            showToastMessage("info", "Sin disponibilidad",
                    "No hay horarios disponibles para este día.");
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
        FacesContext context = FacesContext.getCurrentInstance();
        Paciente pacienteLogueado = (Paciente) context.getExternalContext().getSessionMap().get("usuarioLogueado");

        if (pacienteLogueado == null) {
            citasRegistradas = new ArrayList<>();
            return;
        }

        citasRegistradas = ServiceLocator.getInstanceCitaDAO().obtenerPorPaciente(pacienteLogueado.getId());

        if (citasRegistradas != null) {
            citasRegistradas = citasRegistradas.stream()
                    .filter(c -> c.getEstado() != Cita.EstadoCita.Cancelada)
                    .toList();
        }
    }

    public void cancelarCita() {
        try {
            if (citaSeleccionada == null || citaSeleccionada.getId() == null) {
                showToastMessage("warn", "Aviso", "Debe seleccionar una cita para cancelar.");
                return;
            }
            if (citaSeleccionada.getEstado() == Cita.EstadoCita.Cancelada) {
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

    // --- Getters y Setters ---
    public Cita getCitaSeleccionada() { return citaSeleccionada; }
    public void setCitaSeleccionada(Cita citaSeleccionada) { this.citaSeleccionada = citaSeleccionada; }
    public void setListaDentistas(List<Empleado> listaDentistas) {
        this.listaDentistas = listaDentistas;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
        if (fecha != null) cargarHorasDisponibles();
    }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public Paciente getPacienteObjetivo() { return pacienteObjetivo; }
    public void setPacienteObjetivo(Paciente pacienteObjetivo) { this.pacienteObjetivo = pacienteObjetivo; }
    public Empleado getDentistaSeleccionado() { return dentistaSeleccionado; }
    public void setDentistaSeleccionado(Empleado dentistaSeleccionado) { this.dentistaSeleccionado = dentistaSeleccionado; }
    public List<Empleado> getListaDentistas() { return listaDentistas; }

    public List<String> getHorasDisponibles() { return horasDisponibles; }
    public List<LocalDate> getFechasDisponibles() { return fechasDisponibles; }
    public List<Cita> getCitasRegistradas() { return citasRegistradas; }

    public Date getManana() {
        LocalDate manana = LocalDate.now().plusDays(1);
        return Date.from(manana.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
