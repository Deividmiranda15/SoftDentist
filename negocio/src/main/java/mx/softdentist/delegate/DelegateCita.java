package mx.softdentist.delegate;

import mx.softdentist.dao.CitaDAO;
import mx.softdentist.entidad.Cita;
import mx.softdentist.integration.ServiceFacadeLocator;


import java.util.List;

public class DelegateCita {
    public void saveCita(Cita cita) {
        ServiceFacadeLocator.getInstanceFacadeCita().saveCita(cita);
    }

    public List<Cita> consultarCitasPorPaciente(int idPaciente) {
        List<Cita> citas = ServiceFacadeLocator.getInstanceFacadeCita().consultarCitasPorPaciente(idPaciente);
        return citas;
    }

    public List<Cita> obtenerTodasCitas() {
        List<Cita> todasLasCitas = ServiceFacadeLocator.getInstanceFacadeCita().obtenerTodasLasCitas();
        return todasLasCitas;
    }
    public void actualizarCita(Cita cita) {
        ServiceFacadeLocator.getInstanceFacadeCita().updateCita(cita);
    }
}

