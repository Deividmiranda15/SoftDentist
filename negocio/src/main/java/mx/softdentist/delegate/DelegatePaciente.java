package mx.softdentist.delegate;

import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import java.util.List;

public class DelegatePaciente {

    public Paciente login(String password, String correo) {
        Paciente paciente = new Paciente();
        List<Paciente> usuarios = ServiceLocator.getInstancePacienteDAO().findAll();

        for (Paciente us : usuarios) {
            if (us.getContrasena().equalsIgnoreCase(password) && us.getCorreo().equalsIgnoreCase(correo)) {
                paciente = us;
            }
        }
        return  paciente;
    }

    public void saveUsario(Paciente usuario) {
        ServiceLocator.getInstancePacienteDAO().save(usuario);
    }
}

