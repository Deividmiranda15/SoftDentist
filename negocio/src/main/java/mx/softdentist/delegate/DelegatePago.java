package mx.softdentist.delegate;

import mx.softdentist.entidad.Pago;
import mx.softdentist.facade.FacadePago;
import mx.softdentist.integration.ServiceFacadeLocator;
import java.util.List;

public class DelegatePago {

    private final FacadePago facadePago;

    public DelegatePago() {
        // Obtenemos la instancia a través del Locator
        this.facadePago = ServiceFacadeLocator.getInstance().getFacadePago();
    }

    public List<Pago> obtenerTodos() {
        return facadePago.obtenerTodos();
    }

    public void registrarPago(Pago pago) {
        facadePago.guardar(pago);
    }
}