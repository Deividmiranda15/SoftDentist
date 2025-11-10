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

    public Cita findCitaById(Integer id) {
        return ServiceLocator.getInstanceCitaDAO().find(id).orElse(null);
    }

    public List<Cita> obtenerTodasLasCitas() {
        return ServiceLocator.getInstanceCitaDAO().obtenerTodasConPacientes(); // Usar el nuevo método
    }

    public List<Cita> consultarCitasPorPaciente(int idPaciente) {
        return ServiceLocator.getInstanceCitaDAO().obtenerPorPacienteConDetalles(idPaciente); // Usar el nuevo método
    }

    // Nuevo método para actualizar cita
    public boolean actualizarCita(Cita cita) {
        try {
            ServiceLocator.getInstanceCitaDAO().update(cita);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}