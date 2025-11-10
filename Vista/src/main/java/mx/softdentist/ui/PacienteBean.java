package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.dao.PacienteDAO;
import mx.softdentist.integration.ServiceLocator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named("pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable {

    private Paciente nuevoPaciente;
    private List<Paciente> listaPacientes;
    private Paciente pacienteAEditar;

    public PacienteBean() {
        nuevoPaciente = new Paciente();
        pacienteAEditar = new Paciente();
    }

    @PostConstruct
    public void init() {
        PacienteDAO dao = ServiceLocator.getInstancePacienteDAO();
        listaPacientes = dao.obtenerTodos();
    }

    public void guardarPaciente() {
        try {
            PacienteDAO dao = ServiceLocator.getInstancePacienteDAO();
            dao.save(nuevoPaciente);

            listaPacientes = dao.obtenerTodos(); // refresca tabla
            nuevoPaciente = new Paciente(); // limpia formulario

            //mensaje de exito
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exito", "Paciente guardado"));

            // mensaje de exito
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente registrado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();

            //menaje de error
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el paciente"));
            // mensaje de fallo
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar al paciente. Intente de nuevo.");
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public String irAEditar(Paciente p) {
        // Este es para luego hacer la modificacion con un editarPaciente.xhtml
        return "modificacion_pacientes.xhtml?faces-redirect=true&id=" + p.getId();
    }

    public void cargarDatosParaEditar() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                PacienteDAO dao = ServiceLocator.getInstancePacienteDAO();
                this.pacienteAEditar = dao.find(id).orElse(new Paciente());
            } catch (NumberFormatException e) {
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "ID de paciente inválido.");
            }
        }
    }

    public String actualizarPaciente() {
        try {
            PacienteDAO dao = ServiceLocator.getInstancePacienteDAO();
            dao.update(pacienteAEditar);
            return "pacientes.xhtml?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar.");
            return null;
        }
    }

    // Getters y setters
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
    public Paciente getPacienteAEditar() {
        return pacienteAEditar;
    }
    public void setPacienteAEditar(Paciente pacienteAEditar) {
        this.pacienteAEditar = pacienteAEditar;
    }
}