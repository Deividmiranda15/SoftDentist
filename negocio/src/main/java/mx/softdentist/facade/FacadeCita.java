package mx.softdentist.facade;

import mx.softdentist.entidad.Cita;
import mx.softdentist.integration.ServiceLocator;

public class FacadeCita {
    public void saveCita(Cita cita) {
        ServiceLocator.getInstanceCitaDAO().save(cita);
    }
}