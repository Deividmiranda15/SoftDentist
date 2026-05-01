package mx.softdentist.ui;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Named("pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable{

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
            if (nuevoPaciente.getFechaNacimiento() != null) {
                LocalDate limiteSeisMeses = LocalDate.now().minusMonths(6);

                // Validamos que la fecha de nacimiento NO sea después del límite de 6 meses
                if (nuevoPaciente.getFechaNacimiento().isAfter(limiteSeisMeses)) {
                    addGlobalMessage(FacesMessage.SEVERITY_WARN, "Paciente muy joven",
                            "El paciente debe tener al menos 6 meses de edad");
                    return;
                }
            }

            // Resto de tu código de guardado...
            pacienteDAO.save(nuevoPaciente);
            listaPacientes = pacienteDAO.obtenerTodos();
            nuevoPaciente = new Paciente();
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente registrado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar al paciente");
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public String irAEditar(Paciente p) {
        System.out.println("Navegando a editar paciente ID: " + p.getId());
        return "editarPaciente.xhtml?faces-redirect=true&id=" + p.getId();
    }
        //Calcula la fecha de 6 meses (tomando en cuenta logica de crecimiento dental)
    public LocalDate getMaxDate() {
        return LocalDate.now().minusMonths(6);
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