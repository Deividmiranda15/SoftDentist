package mx.softdentist.dao;
import java.time.LocalDate;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
public class GestorEstadoPacientes {

    @PersistenceContext
    private EntityManager em;

    // Se ejecuta automáticamente todos los días a medianoche
    @Schedule(hour = "0", minute = "0", persistent = false, info = "Actualización de inactividad")
    public void actualizarPacientesInactivos() {
        LocalDate limiteInactividad = LocalDate.now().minusMonths(3);

        // Actualiza la BD
        int actualizados = em.createQuery(
                        "UPDATE Paciente p SET p.estado = 'Inactivo' " +
                                "WHERE p.fechaUltimaCita <= :limite AND p.estado = 'Activo'")
                .setParameter("limite", limiteInactividad)
                .executeUpdate();

        System.out.println("Se actualizaron " + actualizados + " pacientes a Inactivo.");
    }
}
