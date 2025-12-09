package mx.softdentist.delegate;

import mx.softdentist.entidad.Producto;
import mx.softdentist.integration.ServiceFacadeLocator;

import java.util.List;

public class DelegateProducto {
    public void saveProducto(Producto producto) {
        ServiceFacadeLocator.getInstanceFacadeProducto().saveProducto(producto);
    }

    public Producto findProductoById(int idProducto) {
        return ServiceFacadeLocator.getInstanceFacadeProducto().findProductoById(idProducto);
    }

    public List<Producto> obtenerTodosLosProductos() {
        return ServiceFacadeLocator.getInstanceFacadeProducto().obtenerTodosLosProductos();
    }
}
