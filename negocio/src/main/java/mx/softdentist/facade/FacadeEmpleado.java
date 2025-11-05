package mx.softdentist.facade;

import mx.softdentist.delegate.DelegateEmpleado;
import mx.softdentist.entidad.Empleado;

import java.util.List;

public class FacadeEmpleado {
    private final DelegateEmpleado delegateEmpleado;

    public FacadeEmpleado() {
        this.delegateEmpleado = new DelegateEmpleado();

    }

    public Empleado login(String password, String correo){
        return delegateEmpleado.login(password, correo);

    }

    public void saveUsario(Empleado empleado){
        delegateEmpleado.saveUsario(empleado);

    }

    public List<Empleado> consultarTodosLosEmpleados() {
        return delegateEmpleado.consultarTodosLosEmpleados();
    }

}

