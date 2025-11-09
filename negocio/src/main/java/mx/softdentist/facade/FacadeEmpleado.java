package mx.softdentist.facade;

import mx.softdentist.delegate.DelegateEmpleado;
import mx.softdentist.entidad.Empleado;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class FacadeEmpleado {


    public Empleado login(String password, String correo) {
        List<Empleado> todos = ServiceLocator.getInstanceEmpleadoDAO().obtenerTodos();
        for (Empleado emp : todos) {
            // Asegúrate de que los getters no sean nulos
            if (emp.getPassword() != null && emp.getCorreo() != null &&
                    emp.getPassword().equals(password) && emp.getCorreo().equalsIgnoreCase(correo)) {
                return emp;
            }
        }
        return null; // Si no se encuentra
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
}

