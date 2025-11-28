package mx.softdentist.facade;

import mx.softdentist.delegate.DelegateEmpleado;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class FacadeEmpleado {
    private final DelegateEmpleado delegateEmpleado;

    public FacadeEmpleado() {
        this.delegateEmpleado = new DelegateEmpleado();

    }

    public Empleado login(String password, String correo) {
        return delegateEmpleado.login(password, correo);
    }
    public void saveEmpleado(Empleado empleado) {
        ServiceLocator.getInstanceEmpleadoDAO().save(empleado);
    }

    public void updateEmpleado(Empleado empleado) {
        ServiceLocator.getInstanceEmpleadoDAO().update(empleado);
    }

    public Empleado findEmpleadoById(int id) {
        return ServiceLocator.getInstanceEmpleadoDAO().find(id).orElse(null);
    }

    public List<Empleado> obtenerTodosEmpleados() {
        return ServiceLocator.getInstanceEmpleadoDAO().obtenerTodos();
    }


        public void saveUsario(Empleado empleado){
            delegateEmpleado.saveUsario(empleado);

        }

        public List<Empleado> consultarTodosLosEmpleados() {
            return delegateEmpleado.consultarTodosLosEmpleados();
        }
}

