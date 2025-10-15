package mx.softdentist.delegate;

import mx.softdentist.entidad.Empleado;
import mx.softdentist.integration.ServiceLocator;
import java.util.List;

import java.util.List;
public class DelegateEmpleado {

public Empleado login(String password, String correo){
    Empleado empleado = new Empleado();
    List<Empleado> usuarios = ServiceLocator.getInstanceEmpleadoDAO().findAll();

    for(Empleado us:usuarios){
        if(us.getContrasena().equalsIgnoreCase(password) && us.getCorreo().equalsIgnoreCase(correo)){
            empleado = us;
        }
    }
    return empleado;
}

public void saveUsario(Empleado empleado){
    ServiceLocator.getInstanceEmpleadoDAO().save(empleado);
}

}