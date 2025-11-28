package mx.softdentist.delegate;

import mx.softdentist.entidad.Administrador;
import mx.softdentist.integration.ServiceLocator;
import java.util.List;


public class DelegateAdministrador {

    public Administrador login(String password, String correo){
        Administrador admin = new Administrador();
        List<Administrador> usuarios = ServiceLocator.getInstanceAdministradorDAO().findAll();

        for(Administrador us:usuarios){
            if(us.getPassword().equalsIgnoreCase(password) && us.getCorreo().equalsIgnoreCase(correo)){
                admin = us;
            }
        }
        return admin;
        }
    public void saveUsuario(Administrador administrador){
        ServiceLocator.getInstanceAdministradorDAO().save(administrador);
    }

}
