package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.facade.FacadeEmpleado;
import mx.softdentist.integration.ServiceLocator;
import mx.softdentist.dao.EmpleadoDAO;

import java.io.Serializable;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named("empleadoBean")
@ViewScoped
public class EmpleadoBean implements Serializable {

    private FacadeEmpleado facadeEmpleado;

    // Propiedades principales
    private Empleado nuevoEmpleado;
    private List<Empleado> listaEmpleados;
    private EmpleadoDAO empleadoDAO;
    private List<String> puestosDisponibles;
    private Empleado empleadoAEditar;

    // --- NUEVA PROPIEDAD PARA EL CONTROL DE TABS ---
    private String vistaActual;

    public EmpleadoBean() {
        empleadoDAO = ServiceLocator.getInstanceEmpleadoDAO();
        nuevoEmpleado = new Empleado();
        empleadoAEditar = new Empleado();
        listaEmpleados = new ArrayList<>();

        // Inicializamos la vista por defecto en CONSULTA
        this.vistaActual = "CONSULTA";

        // Puestos para empleados
        puestosDisponibles = new ArrayList<>();
        puestosDisponibles.add("Dentista");
        puestosDisponibles.add("Asistente Dental");
        puestosDisponibles.add("Recepcionista");
        puestosDisponibles.add("Mensajero");
    }

    @PostConstruct
    public void init() {
        facadeEmpleado = new FacadeEmpleado();
        // Cargar la lista al iniciar
        try {
            listaEmpleados = empleadoDAO.obtenerTodos();
        } catch (Exception e) {
            System.out.println("Error al cargar empleados: " + e.getMessage());
            listaEmpleados = new ArrayList<>();
        }
    }

    // --- NUEVO MÉTODO PARA CAMBIAR ENTRE PESTAÑAS ---
    public void cambiarVista(String vista) {
        this.vistaActual = vista;

        // Si cambiamos a consulta, refrescamos la lista por si hubo cambios
        if ("CONSULTA".equals(vista)) {
            this.listaEmpleados = empleadoDAO.obtenerTodos();
        }
        // Si cambiamos a alta, limpiamos el formulario
        if ("ALTA".equals(vista)) {
            this.nuevoEmpleado = new Empleado();
        }
    }

    public void guardarEmpleado() {
        try {
            empleadoDAO.save(nuevoEmpleado);
            listaEmpleados = empleadoDAO.obtenerTodos(); // refresca tabla
            nuevoEmpleado = new Empleado(); // limpia formulario

             this.vistaActual = "CONSULTA";

            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleado registrado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar.");
        }
    }

    public void actualizarEmpleado() {
        try {
            empleadoDAO.update(empleadoAEditar);
            listaEmpleados = empleadoDAO.obtenerTodos(); // refrescar lista
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleado actualizado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar.");
        }
    }

    // Método auxiliar para mensajes
    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // --- Getters y Setters ---

    public String getVistaActual() {
        return vistaActual;
    }

    public void setVistaActual(String vistaActual) {
        this.vistaActual = vistaActual;
    }

    public Empleado getNuevoEmpleado() { return nuevoEmpleado; }
    public void setNuevoEmpleado(Empleado nuevoEmpleado) { this.nuevoEmpleado = nuevoEmpleado; }

    public List<String> getPuestosDisponibles() { return puestosDisponibles; }

    public List<Empleado> getListaEmpleados() { return listaEmpleados; }
    public void setListaEmpleados(List<Empleado> listaEmpleados) { this.listaEmpleados = listaEmpleados; }

    public Empleado getEmpleadoAEditar() { return empleadoAEditar; }
    public void setEmpleadoAEditar(Empleado empleadoAEditar) { this.empleadoAEditar = empleadoAEditar; }
}