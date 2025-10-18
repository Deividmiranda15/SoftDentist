package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.integration.ServiceLocator;
import mx.softdentist.dao.EmpleadoDAO;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.List;

@Named("empleadoBean")
@RequestScoped
public class EmpleadoBean {

    private Empleado nuevoEmpleado;
    private List<Empleado> listaEmpleados;
    private EmpleadoDAO empleadoDAO;
    private List<String> puestosDisponibles;

    public EmpleadoBean() {
        empleadoDAO = ServiceLocator.getInstanceEmpleadoDAO();
        nuevoEmpleado = new Empleado();
        listaEmpleados = new ArrayList<>();

        // Puestos para empleados
        puestosDisponibles = new ArrayList<>();
        puestosDisponibles.add("Dentista");
        puestosDisponibles.add("Asistente Dental");
        puestosDisponibles.add("Recepcionista");
        puestosDisponibles.add("Mensajero");
    }

    @PostConstruct
    public void init() {
        listaEmpleados = empleadoDAO.obtenerTodos();
    }

    public void guardarEmpleado() {
        try {
            empleadoDAO.save(nuevoEmpleado);
            listaEmpleados = empleadoDAO.obtenerTodos(); // refresca tabla
            nuevoEmpleado = new Empleado(); // limpia formulario
            // mensaje de exito
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleado registrado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            // mensaje de fallo
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar al Empleado. Intente de nuevo.");
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public String irAEditar(Empleado e) {
        // Este es para luego hacer la modificacion con un editarEmpleado.xhtml
        return "editarEmpleado.xhtml?faces-redirect=true&id=" + e.getId();
    }

    // Getters y setters
    public Empleado getNuevoEmpleado() {
        return nuevoEmpleado;
    }

    public void setNuevoEmpleado(Empleado nuevoEmpleado) {
        this.nuevoEmpleado = nuevoEmpleado;
    }

    public List<String> getPuestosDisponibles() {
        return puestosDisponibles;
    }

    public List<Empleado> getListaEmpleados() {
        return listaEmpleados;
    }

    public void setListaEmpleados(List<Empleado> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }
}
