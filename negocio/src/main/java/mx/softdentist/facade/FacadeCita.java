package mx.softdentist.facade;

import mx.softdentist.entidad.Cita;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class FacadeCita {
    public boolean saveCita(Cita cita) {
        try {
            ServiceLocator.getInstanceCitaDAO().save(cita);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Puedes agregar más métodos según necesites
    public Cita findCitaById(Integer id) {
        return ServiceLocator.getInstanceCitaDAO().find(id).orElse(null);
    }
    public List<Cita> obtenerTodasLasCitas() {
        return ServiceLocator.getInstanceCitaDAO().obtenerTodos();
    }
}