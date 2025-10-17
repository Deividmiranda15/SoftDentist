package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.facade.FacadeEmpleado;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class EmpleadoBean implements Serializable {

    private FacadeEmpleado facadeEmpleado;

    private List<Empleado> listaEmpleados;

    @PostConstruct
    public void init() {
        facadeEmpleado = new FacadeEmpleado();
        listaEmpleados = facadeEmpleado.consultarTodosLosEmpleados();
    }

    public List<Empleado> getListaEmpleados() {
        return listaEmpleados;
    }

    public void setListaEmpleados(List<Empleado> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }
}