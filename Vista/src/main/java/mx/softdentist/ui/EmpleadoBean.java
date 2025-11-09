package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.dao.EmpleadoDAO; // Seguimos usando el DAO
import mx.softdentist.integration.ServiceLocator; // Seguimos usando el ServiceLocator

import java.io.Serializable;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named("empleadoBean")
@ViewScoped
public class EmpleadoBean implements Serializable {

    // --- ATRIBUTOS ---
    private Empleado nuevoEmpleado;
    private List<Empleado> listaEmpleados;
    private List<String> puestosDisponibles;
    private Empleado empleadoAEditar;

    public EmpleadoBean() {

        nuevoEmpleado = new Empleado();
        empleadoAEditar = new Empleado();

        puestosDisponibles = new ArrayList<>();
        puestosDisponibles.add("Dentista");
        puestosDisponibles.add("Asistente Dental");
        puestosDisponibles.add("Recepcionista");
        puestosDisponibles.add("Mensajero");
    }

    @PostConstruct
    public void init() {
        //obtenemos el DAO aquí
        EmpleadoDAO dao = ServiceLocator.getInstanceEmpleadoDAO();
        listaEmpleados = dao.obtenerTodos();
    }

    public void guardarEmpleado() {
        try {

            EmpleadoDAO dao = ServiceLocator.getInstanceEmpleadoDAO();
            dao.save(nuevoEmpleado);
            listaEmpleados = dao.obtenerTodos(); // refresca tabla
            nuevoEmpleado = new Empleado(); // limpia formulario
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleado registrado.");
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar.");
        }
    }

    public String irAEditar(Empleado e) {
        return "modificacion_empleados.xhtml?faces-redirect=true&id=" + e.getId();
    }


    public void cargarDatosParaEditar() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                EmpleadoDAO dao = ServiceLocator.getInstanceEmpleadoDAO();
                this.empleadoAEditar = dao.find(id).orElse(new Empleado());
            } catch (NumberFormatException e) {
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "ID de empleado inválido.");
            }
        }
    }


    public String actualizarEmpleado() {
        try {

            EmpleadoDAO dao = ServiceLocator.getInstanceEmpleadoDAO();
            dao.update(empleadoAEditar);

            return "empleados.xhtml?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar.");
            return null;
        }
    }


    // Getters y Setters (Sin cambios) ---

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public Empleado getNuevoEmpleado() { return nuevoEmpleado; }
    public void setNuevoEmpleado(Empleado nuevoEmpleado) { this.nuevoEmpleado = nuevoEmpleado; }
    public List<String> getPuestosDisponibles() { return puestosDisponibles; }
    public List<Empleado> getListaEmpleados() { return listaEmpleados; }
    public void setListaEmpleados(List<Empleado> listaEmpleados) { this.listaEmpleados = listaEmpleados; }
    public Empleado getEmpleadoAEditar() { return empleadoAEditar; }
    public void setEmpleadoAEditar(Empleado empleadoAEditar) { this.empleadoAEditar = empleadoAEditar; }
}