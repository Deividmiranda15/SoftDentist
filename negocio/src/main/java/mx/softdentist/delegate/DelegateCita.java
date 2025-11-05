package mx.softdentist.delegate;

import mx.softdentist.entidad.Cita;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class DelegateCita {
    public void saveCita(Cita cita) {
        ServiceLocator.getInstanceCitaDAO().save(cita);
    }

    public List<Cita> consultarCitasPorPaciente(int idPaciente) {

        List<Cita> citas = ServiceLocator.getInstanceCitaDAO().findCitasByPacienteId(idPaciente);
        return citas;
        //xd
    }
}

