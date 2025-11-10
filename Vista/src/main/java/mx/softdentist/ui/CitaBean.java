package mx.softdentist.ui;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import org.primefaces.PrimeFaces;

import java.io.File;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("citaBean")
@ViewScoped
public class CitaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private String hora;
    private String motivo;

    private List<String> horasDisponibles;
    private List<LocalDate> fechasDisponibles;
    private List<Cita> citasRegistradas;
    private Cita citaSeleccionada;

    public CitaBean() {
        cargarFechasDisponibles();
        cargarCitasRegistradas();
    }

    // --- Solicitar cita asociada al paciente en sesión ---
    public void solicitarCita() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            Paciente pacienteLogueado = (Paciente) context.getExternalContext().getSessionMap().get("usuarioLogueado");

            if (pacienteLogueado == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error de sesión", "No se encontró el paciente autenticado. Inicie sesión nuevamente."));
                return;
            }

            // Validaciones
            if (fecha == null) {
                showToastMessage("error", "Error de Validación", "Debe seleccionar una fecha.");
                return;
            }
            if (!fecha.isAfter(LocalDate.now())) {
                showToastMessage("error", "Fecha inválida", "No puede agendar citas el mismo día o en días pasados.");
                return;
            }
            if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
                showToastMessage("error", "Horario no disponible", "No se pueden agendar citas los domingos.");
                return;
            }
            if (hora == null || hora.isEmpty()) {
                showToastMessage("error", "Error de Validación", "Debe seleccionar una hora.");
                return;
            }
            if (motivo == null || motivo.trim().isEmpty()) {
                showToastMessage("error", "Error de Validación", "Debe indicar el motivo de la cita.");
                return;
            }

            Cita cita = new Cita();
            cita.setFecha(fecha);
            cita.setHora(LocalTime.parse(hora));
            cita.setMotivo(motivo);
            cita.setEstado("Pendiente");

            cita.setIdPaciente(pacienteLogueado);

            ServiceLocator.getInstanceCitaDAO().save(cita);

            showToastMessage("success", "¡Cita solicitada con éxito!",
                    "Su cita para el " + fecha + " a las " + hora + " ha sido registrada correctamente.");

            limpiarFormulario();
            cargarCitasRegistradas();

        } catch (Exception e) {
            showToastMessage("error", "Error del Sistema",
                    "Ocurrió un error al solicitar la cita: " + e.getMessage());
            e.printStackTrace();
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
                    .filter(c -> !"Cancelada".equalsIgnoreCase(c.getEstado()))
                    .toList();
        }
    }

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

    // MÉTODO CORREGIDO - Sin ServletContext
    public boolean isRecetaDisponible(Cita cita) {
        if (cita == null || !"Completada".equals(cita.getEstado())) {
            return false;
        }

        try {
            // CORRECCIÓN: Usar getRealPath() en lugar de ServletContext
            String realPath = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRealPath("/resources/recetas/");

            if (realPath == null) {
                return false;
            }

            String nombreArchivo = "receta_cita" + cita.getId() + ".pdf";
            String rutaArchivo = realPath + File.separator + nombreArchivo;

            File archivo = new File(rutaArchivo);
            return archivo.exists() && archivo.isFile();
        } catch (Exception e) {
            System.err.println("Error en isRecetaDisponible: " + e.getMessage());
            return false;
        }
    }

    public void descargarReceta(Cita cita) {
        try {
            if (!isRecetaDisponible(cita)) {
                showToastMessage("warn", "Receta no disponible",
                        "La receta para esta cita no está disponible aún.");
                return;
            }
            // La descarga se maneja automáticamente con el link
        } catch (Exception e) {
            showToastMessage("error", "Error", "No se pudo descargar la receta.");
            e.printStackTrace();
        }
    }

    public String getRutaReceta(Cita cita) {
        if (!isRecetaDisponible(cita)) {
            return "#";
        }
        return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() +
                "/resources/recetas/receta_cita" + cita.getId() + ".pdf";
    }

    // --- Getters y Setters ---
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

    public List<String> getHorasDisponibles() { return horasDisponibles; }
    public List<LocalDate> getFechasDisponibles() { return fechasDisponibles; }
    public List<Cita> getCitasRegistradas() { return citasRegistradas; }

    public Date getManana() {
        LocalDate manana = LocalDate.now().plusDays(1);
        return Date.from(manana.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}