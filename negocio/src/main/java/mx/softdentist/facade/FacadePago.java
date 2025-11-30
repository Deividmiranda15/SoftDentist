package mx.softdentist.facade;

import mx.softdentist.entidad.Pago;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class FacadePago {

    public boolean savePago(Pago pago) {
        try {
            ServiceLocator.getInstancePagoDAO().save(pago);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Pago findPagoById(Integer id) {
        return ServiceLocator.getInstancePagoDAO().find(id).orElse(null);
    }

    public List<Pago> obtenerTodosLosPagos() {
        return ServiceLocator.getInstancePagoDAO().obtenerTodos();
    }
}
