package mx.softdentist.entidad;

import junit.framework.TestCase;

public class EntidadesBasicasTest extends TestCase {

    public void testCitaIniciaConEstadoPendiente() {
        Cita cita = new Cita();

        assertEquals(Cita.EstadoCita.Pendiente, cita.getEstado());
    }

    public void testCitaPermiteCambiarEstadoACompletada() {
        Cita cita = new Cita();

        cita.setEstado(Cita.EstadoCita.Completada);

        assertEquals(Cita.EstadoCita.Completada, cita.getEstado());
    }

    public void testPagoCalculaMontoFinalRestandoCambio() {
        Pago pago = new Pago();
        pago.setMontoRecibido(500.0f);
        pago.setCambioRegresado(125.5f);

        assertEquals(374.5f, pago.getMontoFinal());
    }

    public void testAdministradorInicializaColeccionesNoNulas() {
        Administrador administrador = new Administrador();

        assertNotNull(administrador.getCitas());
        assertNotNull(administrador.getEmpleados());
        assertNotNull(administrador.getPacientes());
    }
}


