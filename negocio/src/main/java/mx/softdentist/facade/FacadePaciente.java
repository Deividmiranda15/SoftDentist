package mx.softdentist.facade;

import mx.softdentist.delegate.DelegatePaciente;
import mx.softdentist.entidad.Paciente;

public class FacadePaciente{
    private final DelegatePaciente delegatePaciente;

    public FacadePaciente() {
        this.delegatePaciente = new DelegatePaciente();
    }

    public Paciente login(String password, String correo){
        return delegatePaciente.login(password, correo);
    }

    public void saveUsario(Paciente empleado){
        delegatePaciente.saveUsario(empleado);
    }

}


