package mx.softdentist.facade;

import mx.softdentist.dao.PacienteDAO;
import mx.softdentist.delegate.DelegatePaciente;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class FacadePaciente{
    private final DelegatePaciente delegatePaciente;

    public FacadePaciente() {
        this.delegatePaciente = new DelegatePaciente();
    }

    public Paciente login(String password, String correo){
        return delegatePaciente.login(password, correo);
    }

    public List<String> getAllEmails() {
        PacienteDAO dao = ServiceLocator.getInstancePacienteDAO();
        return dao.obtenerTodosLosCorreos();
    }

}


