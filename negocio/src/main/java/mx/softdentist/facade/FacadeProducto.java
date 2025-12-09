package mx.softdentist.facade;


import mx.softdentist.entidad.Producto;
import mx.softdentist.integration.ServiceLocator;

import java.util.List;

public class FacadeProducto {

    public boolean saveProducto(Producto producto) {
        try {
            ServiceLocator.getInstanceProductoDAO().save(producto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Producto findProductoById(Integer id) {
        return ServiceLocator.getInstanceProductoDAO().find(id).orElse(null);
    }

    public List<Producto> obtenerTodosLosProductos() {
        return ServiceLocator.getInstanceProductoDAO().obtenerTodos();
    }
}
