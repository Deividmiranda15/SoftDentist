package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import mx.softdentist.dao.PacienteDAO;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.List;

@Named("pacienteBean")
@RequestScoped
public class PacienteBean {

    private Paciente nuevoPaciente;
    private List<Paciente> listaPacientes;
    private PacienteDAO pacienteDAO;

    public PacienteBean() {
        pacienteDAO = ServiceLocator.getInstancePacienteDAO();
        nuevoPaciente = new Paciente();
        listaPacientes = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        listaPacientes = pacienteDAO.obtenerTodos();
    }

    public void guardarPaciente() {
        try {
            pacienteDAO.save(nuevoPaciente);
            listaPacientes = pacienteDAO.obtenerTodos(); // refresca tabla
            nuevoPaciente = new Paciente(); // limpia formulario
            // mensaje de exito
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente registrado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
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
}
