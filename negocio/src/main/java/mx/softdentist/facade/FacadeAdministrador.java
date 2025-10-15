package mx.softdentist.facade;

import mx.softdentist.delegate.DelegateAdministrador;
import mx.softdentist.entidad.Administrador;

public class FacadeAdministrador {
    private final DelegateAdministrador delegateAdministrador;

    public FacadeAdministrador() {
        this.delegateAdministrador = new DelegateAdministrador();
    }

    public Administrador login(String password, String correo){
        return delegateAdministrador.login(password, correo);
    }

    public void saveUsuario(Administrador admin){
        delegateAdministrador.saveUsuario(admin);
    }

}
