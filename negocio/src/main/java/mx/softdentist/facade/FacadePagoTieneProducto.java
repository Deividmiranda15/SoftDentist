package mx.softdentist.facade;

import mx.softdentist.entidad.PagoTieneProducto;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class FacadePagoTieneProducto {
    public boolean savePagoTieneProducto(PagoTieneProducto pagoTieneProducto) {
        try {
            ServiceLocator.getInstancePagoTieneProductoDAO().save(pagoTieneProducto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PagoTieneProducto> findPagoTieneProductoByIdPago(Integer idPago) {
        return ServiceLocator.getInstancePagoTieneProductoDAO().obtenerPorIdPago(idPago);
    }

    public List<PagoTieneProducto> findPagoTieneProductoByIdProducto(Integer idProducto) {
        return ServiceLocator.getInstancePagoTieneProductoDAO().obtenerPorIdProducto(idProducto);
    }

    public PagoTieneProducto findPagoTieneProductoByBothId(Integer idPago, Integer idProducto) {
        return ServiceLocator.getInstancePagoTieneProductoDAO().obtenerPorBothId(idPago, idProducto);
    }

    public List<PagoTieneProducto> obtenerTodosLosPagoTieneProductos() {
        return ServiceLocator.getInstancePagoTieneProductoDAO().obtenerTodos();
    }
}
