package mx.softdentist.facade;

import mx.softdentist.entidad.Cita;
import mx.softdentist.integration.ServiceLocator;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public void updateCita(Cita cita) {
        try {
            ServiceLocator.getInstanceCitaDAO().update(cita);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean reagendarCita(Integer idCita, LocalDate nuevaFecha, LocalTime nuevaHora, String motivo) {
        try {
            return ServiceLocator.getInstanceCitaDAO().reagendarCita(idCita, nuevaFecha, nuevaHora, motivo);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}