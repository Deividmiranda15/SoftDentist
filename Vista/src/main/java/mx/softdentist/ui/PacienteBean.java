package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import mx.softdentist.dao.PacienteDAO;

import java.time.LocalDate;
import java.util.Map;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.primefaces.PF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable {

    private Paciente nuevoPaciente;
    private List<Paciente> listaPacientes;
    private PacienteDAO pacienteDAO;
    private Paciente pacienteAEditar;
    // Esta variable le dice al XHTML qué mostrar: "CONSULTA" o "ALTA"
    private String vistaActual = "CONSULTA";

    public PacienteBean() {
        pacienteDAO = ServiceLocator.getInstancePacienteDAO();
        nuevoPaciente = new Paciente();
        listaPacientes = new ArrayList<>();
        pacienteAEditar = new Paciente();
        // Inicializamos la lista para evitar nulos
        listaPacientes = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        listaPacientes = pacienteDAO.obtenerTodos();
    }

    // --- NUEVO MÉTODO PARA CAMBIAR VISTA ---
    public void cambiarVista(String vista) {
        this.vistaActual = vista;
        // Si vamos a registrar uno nuevo, limpiamos el objeto
        if ("ALTA".equals(vista)) {
            this.nuevoPaciente = new Paciente();
        }
    }

    public void guardarPaciente() {
        try {

            // Edad mínima 6 meses ---
            if (nuevoPaciente.getFechaNacimiento() != null) {
                // Calculamos la fecha límite (hace 6 meses desde hoy)
                LocalDate limiteEdad = LocalDate.now().minusMonths(6);

                // Si la fecha de nacimiento es posterior al límite, el paciente es muy joven
                if (nuevoPaciente.getFechaNacimiento().isAfter(limiteEdad)) {
                    addGlobalMessage(FacesMessage.SEVERITY_WARN, "Validación de Edad",
                            "El paciente debe tener al menos 6 meses de edad para ser registrado");
                    return;
                }
            } else {
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha de nacimiento es obligatoria.");
                return;
            }

            pacienteDAO.save(nuevoPaciente);
            listaPacientes = pacienteDAO.obtenerTodos(); // refresca tabla
            nuevoPaciente = new Paciente(); // limpia formulario

            // Al guardar exitosamente, regresamos a la vista de consulta
            this.vistaActual = "CONSULTA";

            // Mensaje de éxito
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente guardado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            // Mensaje de error
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar al paciente. Intente de nuevo.");
        }
    }

    public void cargarDatosParaEditar() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                // Usamos el DAO para buscar el paciente
                this.pacienteAEditar = pacienteDAO.find(id).orElse(new Paciente());
            } catch (NumberFormatException e) {
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "ID de paciente inválido.");
            }
        }
    }

    public LocalDate getMaxDate() {
        return LocalDate.now().minusMonths(6);
    }


    public void actualizarPaciente() {
        try {
            if (pacienteAEditar.getFechaNacimiento() != null) {
                LocalDate limite = LocalDate.now().minusMonths(6);
                if (pacienteAEditar.getFechaNacimiento().isAfter(limite)) {
                    // 1. Mandamos el mensaje
                    addGlobalMessage(FacesMessage.SEVERITY_WARN, "Error", "Mínimo 6 meses de edad.");

                    // 2. IMPORTANTE: El return detiene el método aquí mismo
                    return;
                }
            }

            pacienteDAO.update(pacienteAEditar);
            listaPacientes = pacienteDAO.obtenerTodos(); // Refrescamos la lista para ver el cambio
            PF.current().executeScript("PF('dlgEditar').hide()");
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente actualizado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar.");
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public String irAEditar(Paciente p) {
        return "editarPaciente.xhtml?faces-redirect=true&id=" + p.getId();
    }

    // --- GETTERS Y SETTERS ---

    public Paciente getNuevoPaciente() {
        return nuevoPaciente;
    }

    public void setNuevoPaciente(Paciente nuevoPaciente) {
        this.nuevoPaciente = nuevoPaciente;
    }

    public List<Paciente> getListaPacientes() {
        return listaPacientes;
    }

    public void setListaPacientes(List<Paciente> listaPacientes) {
        this.listaPacientes = listaPacientes;
    }

    public Paciente getPacienteAEditar() { return pacienteAEditar; }

    public void setPacienteAEditar(Paciente pacienteAEditar) { this.pacienteAEditar = pacienteAEditar; }

    // --- GETTERS Y SETTERS DE LA NUEVA PROPIEDAD ---
    public String getVistaActual() {
        return vistaActual;
    }

    public void setVistaActual(String vistaActual) {
        this.vistaActual = vistaActual;
    }
}