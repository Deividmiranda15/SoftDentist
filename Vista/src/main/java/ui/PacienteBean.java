package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import mx.softdentist.dao.PacienteDAO;

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
            pacienteDAO.save(nuevoPaciente); // << aquí usas save en lugar de create
            listaPacientes = pacienteDAO.obtenerTodos(); // refresca tabla
            nuevoPaciente = new Paciente(); // limpia formulario
        } catch (Exception e) {
            e.printStackTrace();
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
