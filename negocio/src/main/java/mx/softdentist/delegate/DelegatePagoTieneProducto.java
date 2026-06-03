package mx.softdentist.delegate;

import mx.softdentist.entidad.PagoTieneProducto;
import mx.softdentist.integration.ServiceFacadeLocator;

import java.util.List;

public class DelegatePagoTieneProducto {
    public void savePagoTieneProducto(PagoTieneProducto pagoTieneProducto) {
        ServiceFacadeLocator.getInstanceFacadePagoTieneProducto().savePagoTieneProducto(pagoTieneProducto);
    }

    public List<PagoTieneProducto> findPagoTieneProductoByIdPago(int idPago) {
        return ServiceFacadeLocator.getInstanceFacadePagoTieneProducto().findPagoTieneProductoByIdPago(idPago);
    }

    public List<PagoTieneProducto> findPagoTieneProductoByIdProducto(int idProducto) {
        return ServiceFacadeLocator.getInstanceFacadePagoTieneProducto().findPagoTieneProductoByIdProducto(idProducto);
    }

    public PagoTieneProducto findPagoTieneProductoByBothId(int idPago, int idProducto) {
        return ServiceFacadeLocator.getInstanceFacadePagoTieneProducto().findPagoTieneProductoByBothId(idPago, idProducto);
    }

    public List<PagoTieneProducto> obtenerTodosLosPagoTieneProductos() {
        return ServiceFacadeLocator.getInstanceFacadePagoTieneProducto().obtenerTodosLosPagoTieneProductos();
    }
}
