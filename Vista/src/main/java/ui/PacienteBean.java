package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import mx.softdentist.dao.PacienteDAO;

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

    public PacienteBean() {
        //listaPacientes = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        pacienteDAO = ServiceLocator.getInstancePacienteDAO();
        nuevoPaciente = new Paciente();
        listaPacientes = pacienteDAO.obtenerTodos();
    }

    public void guardarPaciente() {

        try {
            pacienteDAO.save(nuevoPaciente); // << aquí usas save en lugar de create
            listaPacientes = pacienteDAO.obtenerTodos(); // refresca tabla
            nuevoPaciente = new Paciente(); // limpia formulario

            //mensaje de exito
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exito", "Paciente guardado"));

        } catch (Exception e) {
            e.printStackTrace();

            //menaje de error
            FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el paciente"));
        }
    }

    public String irAEditar(Paciente p) {
        // Aquí más adelante puedes redirigir a editarPaciente.xhtml
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
