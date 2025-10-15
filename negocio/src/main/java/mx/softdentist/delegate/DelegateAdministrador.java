package mx.softdentist.delegate;

import mx.softdentist.entidad.Administrador;
import mx.softdentist.integration.ServiceLocator;
import java.util.List;


public class DelegateAdministrador {

    public Administrador login(String password, String correo){
        Administrador usuario = new Administrador();
        List<Administrador> usuarios = ServiceLocator.getInstanceAdministradorDAO().findAll();

        for(Administrador us:usuarios){
            if(us.getContrasena().equalsIgnoreCase(password) && us.getCorreo().equalsIgnoreCase(correo)){
                usuario = us;
            }
        }
        return usuario;
        }
    public void saveUsuario(Administrador usuario){
        ServiceLocator.getInstanceAdministradorDAO().save(usuario);
    }

}
