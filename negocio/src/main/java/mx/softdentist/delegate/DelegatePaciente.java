package mx.softdentist.delegate;

import mx.softdentist.entidad.Paciente;
import mx.softdentist.facade.FacadePaciente;
import mx.softdentist.integration.ServiceFacadeLocator;
import mx.softdentist.integration.ServiceLocator;
import java.util.List;

public class DelegatePaciente {

    public Paciente login(String password, String correo) {
        Paciente paciente = new Paciente();
        List<Paciente> usuarios = ServiceLocator.getInstancePacienteDAO().findAll();

        for (Paciente us : usuarios) {
            if (us.getPassword().equalsIgnoreCase(password) && us.getCorreo().equalsIgnoreCase(correo)) {
                paciente = us;
            }
        }
        return  paciente;
    }

    public void saveUsuario(Paciente usuario) { // <-- También corregí el nombre del método
            ServiceLocator.getInstancePacienteDAO().save(usuario);
        }

    public List<String> obtenerListaCorreos() {
        FacadePaciente facade = ServiceFacadeLocator.getInstanceFacadePaciente();
        return facade.getAllEmails();
    }

}

