package mx.softdentist.delegate;

import mx.softdentist.entidad.Cita;
import mx.softdentist.integration.ServiceLocator;

public class DelegateCita {
    public void saveCita(Cita cita) {
        ServiceLocator.getInstanceCitaDAO().save(cita);
    }
}
