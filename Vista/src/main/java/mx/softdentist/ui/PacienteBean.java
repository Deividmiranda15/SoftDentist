package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import mx.softdentist.dao.PacienteDAO;
import java.util.Map;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable{

    private Paciente nuevoPaciente;
    private List<Paciente> listaPacientes;
    private PacienteDAO pacienteDAO;
    private Paciente pacienteAEditar;

    public PacienteBean() {
        pacienteDAO = ServiceLocator.getInstancePacienteDAO();
        nuevoPaciente = new Paciente();
        listaPacientes = new ArrayList<>();
        pacienteAEditar = new Paciente();
    }

    @PostConstruct
    public void init() {
        listaPacientes = pacienteDAO.obtenerTodos();
    }

    public void guardarPaciente() {
        try {
            System.out.println("Datos del paciente: " + nuevoPaciente);

            pacienteDAO.save(nuevoPaciente);
            listaPacientes = pacienteDAO.obtenerTodos();
            nuevoPaciente = new Paciente();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente guardado correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR al guardar: " + e.getMessage());

            // Mensaje de error
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el paciente: " + e.getMessage()));
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

    public void actualizarPaciente() {
        try {
            System.out.println("Paciente a editar: " + pacienteAEditar);

            pacienteDAO.update(pacienteAEditar);
            listaPacientes = pacienteDAO.obtenerTodos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente actualizado correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar: " + e.getMessage()));
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public String irAEditar(Paciente p) {
        // Este es para luego hacer la modificacion con un editarPaciente.xhtml
        return "editarPaciente.xhtml?faces-redirect=true&id=" + p.getId();
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

    public Paciente getPacienteAEditar() { return pacienteAEditar; }

    public void setPacienteAEditar(Paciente pacienteAEditar) { this.pacienteAEditar = pacienteAEditar; }
}