package mx.softdentist.entidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EntidadesBasicasTest {

    @Test
    void citaIniciaConEstadoPendiente() {
        Cita cita = new Cita();

        assertEquals(Cita.EstadoCita.Pendiente, cita.getEstado());
    }

    @Test
    void citaPermiteCambiarEstadoACompletada() {
        Cita cita = new Cita();

        cita.setEstado(Cita.EstadoCita.Completada);

        assertEquals(Cita.EstadoCita.Completada, cita.getEstado());
    }

    @Test
    void pagoCalculaMontoFinalRestandoCambio() {
        Pago pago = new Pago();
        pago.setMontoRecibido(500.0f);
        pago.setCambioRegresado(125.5f);

        assertEquals(374.5f, pago.getMontoFinal());
    }

    @Test
    void administradorInicializaColeccionesNoNulas() {
        Administrador administrador = new Administrador();

        assertNotNull(administrador.getCitas());
        assertNotNull(administrador.getEmpleados());
        assertNotNull(administrador.getPacientes());
    }
}

