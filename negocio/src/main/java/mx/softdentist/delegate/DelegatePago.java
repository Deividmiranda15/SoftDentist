package mx.softdentist.delegate;

import mx.softdentist.entidad.Pago;
import mx.softdentist.integration.ServiceFacadeLocator;

import java.util.List;

public class DelegatePago {
    public void savePago(Pago pago) {
        ServiceFacadeLocator.getInstanceFacadePago().savePago(pago);
    }

    public Pago findPagoById(int idPago) {
        return ServiceFacadeLocator.getInstanceFacadePago().findPagoById(idPago);
    }

    public List<Pago> obtenerTodosLosPagos() {
        return ServiceFacadeLocator.getInstanceFacadePago().obtenerTodosLosPagos();
    }
}
