package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.entidad.Producto;
import mx.softdentist.dao.ProductoDAO;
import mx.softdentist.integration.ServiceLocator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

@Named("productoBean")
@ViewScoped
public class ProductoBean implements Serializable {

    private ProductoDAO productoDAO;

    private Producto nuevoProducto;
    private List<Producto> listaProductos;

    /**
     * Snapshot de la lista tal cual viene de la BD (orden "natural": normalmente por inserción/id).
     * Sirve para regresar a "Sin orden" después de aplicar ordenamientos.
     */
    private List<Producto> listaProductosOriginal;

    // Edición
    private Producto productoAEditar;

    // Orden/Filtro por precio
    private String ordenPrecioActual;

    private String vistaActual;

    public ProductoBean() {
        this.productoDAO = ServiceLocator.getInstanceProductoDAO();
        this.nuevoProducto = new Producto();
        this.productoAEditar = new Producto();
        this.listaProductos = new ArrayList<>();
        this.listaProductosOriginal = new ArrayList<>();
        this.vistaActual = "CONSULTA";
        this.ordenPrecioActual = "NINGUNO";
    }

    @PostConstruct
    public void init() {
        refrescarLista();
    }

    public void cambiarVista(String vista) {
        this.vistaActual = vista;

        if ("CONSULTA".equals(vista)) {
            refrescarLista();
        }
        if ("ALTA".equals(vista)) {
            this.nuevoProducto = new Producto();
        }
    }

    public void guardarProducto() {
        try {
            if (nuevoProducto.getConcepto() == null || nuevoProducto.getConcepto().trim().isEmpty()) {
                addGlobalMessage(FacesMessage.SEVERITY_WARN, "Validación", "El nombre del producto es obligatorio.");
                return;
            }
            if (nuevoProducto.getCosto() == null || nuevoProducto.getCosto() <= 0) {
                addGlobalMessage(FacesMessage.SEVERITY_WARN, "Validación", "El precio debe ser mayor a 0.");
                return;
            }

            productoDAO.save(nuevoProducto);
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto registrado correctamente.");

            // refresca tabla y limpia formulario
            refrescarLista();
            this.nuevoProducto = new Producto();
            this.vistaActual = "CONSULTA";
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el producto.");
        }
    }

    public void actualizarProducto() {
        try {
            if (productoAEditar == null || productoAEditar.getId() == null) {
                addGlobalMessage(FacesMessage.SEVERITY_WARN, "Validación", "Selecciona un producto para editar.");
                return;
            }
            if (productoAEditar.getConcepto() == null || productoAEditar.getConcepto().trim().isEmpty()) {
                addGlobalMessage(FacesMessage.SEVERITY_WARN, "Validación", "El nombre del producto es obligatorio.");
                return;
            }
            if (productoAEditar.getCosto() == null || productoAEditar.getCosto() <= 0) {
                addGlobalMessage(FacesMessage.SEVERITY_WARN, "Validación", "El precio debe ser mayor a 0.");
                return;
            }

            productoDAO.update(productoAEditar);
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado correctamente.");
            refrescarLista();
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el producto.");
        }
    }

    public void eliminarProducto(Producto producto) {
        try {
            if (producto == null || producto.getId() == null) {
                addGlobalMessage(FacesMessage.SEVERITY_WARN, "Validación", "Producto inválido.");
                return;
            }
            productoDAO.delete(producto);
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto eliminado correctamente.");
            refrescarLista();
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el producto.");
        }
    }

    public void onOrdenPrecioChange() {
        // Ordenamos en memoria (NO recargar aquí para evitar re-render confuso)
        aplicarOrdenPrecio();
    }

    private void refrescarLista() {
        try {
            // Guardamos el orden tal cual viene de BD
            List<Producto> desdeBd = productoDAO.obtenerTodos();
            this.listaProductosOriginal = (desdeBd != null) ? new ArrayList<>(desdeBd) : new ArrayList<>();

            // La lista visible parte del orden original y puede ser ordenada después
            this.listaProductos = new ArrayList<>(this.listaProductosOriginal);
            aplicarOrdenPrecio();
        } catch (Exception e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
            this.listaProductos = new ArrayList<>();
            this.listaProductosOriginal = new ArrayList<>();
        }
    }

    private void aplicarOrdenPrecio() {
        if (ordenPrecioActual == null || "NINGUNO".equals(ordenPrecioActual)) {
            // Restaurar el orden original
            if (listaProductosOriginal != null) {
                this.listaProductos = new ArrayList<>(listaProductosOriginal);
            }
            return;
        }

        if (listaProductos == null || listaProductos.size() < 2) {
            return;
        }

        Comparator<Float> cmpCosto = Comparator.nullsLast(Float::compareTo);
        Comparator<Producto> cmp = Comparator.comparing(Producto::getCosto, cmpCosto);

        if ("PRECIO_MAS_ALTO".equals(ordenPrecioActual)) {
            listaProductos.sort(cmp.reversed());
        } else if ("PRECIO_MAS_BAJO".equals(ordenPrecioActual)) {
            listaProductos.sort(cmp);
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Producto getNuevoProducto() {
        return nuevoProducto;
    }

    public void setNuevoProducto(Producto nuevoProducto) {
        this.nuevoProducto = nuevoProducto;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public String getVistaActual() {
        return vistaActual;
    }

    public void setVistaActual(String vistaActual) {
        this.vistaActual = vistaActual;
    }

    public Producto getProductoAEditar() {
        return productoAEditar;
    }

    public void setProductoAEditar(Producto productoAEditar) {
        this.productoAEditar = productoAEditar;
    }

    public String getOrdenPrecioActual() {
        return ordenPrecioActual;
    }

    public void setOrdenPrecioActual(String ordenPrecioActual) {
        this.ordenPrecioActual = ordenPrecioActual;
    }
}




