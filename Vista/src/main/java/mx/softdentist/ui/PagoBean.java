package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import mx.softdentist.delegate.DelegatePago;
import mx.softdentist.delegate.DelegateProducto;
import mx.softdentist.entidad.Pago;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import mx.softdentist.entidad.Producto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator; // Importación necesaria para el sort

@Named("pagoBean")
@ViewScoped
public class PagoBean implements Serializable {

    private Pago nuevoPago;
    private List<Pago> listaPagos;
    private List<Producto> listaProductos;
    private DelegatePago delegatePago;
    private DelegateProducto delegateProducto;
    private Pago pagoAEditar;
    private String vistaActual = "ALTA";
    // PROPIEDAD REQUERIDA QUE FALTABA (O NO SE COMPILÓ)
    private String ordenacionPagosActual = "ID_DESC"; // Por defecto ID descendente (más nuevo)
    private String ordenacionProductosActual = "ID_ASC"; // Por defecto órden alfabético
    private String idProductoString = "";
    private String infoProducto = "";

    public PagoBean() {
        delegatePago = new DelegatePago();  // No estoy seguro si se debería crear el DelegatePago directamente o si estaría mejor crear una clase ServiceDelegateLocator.
        delegateProducto = new DelegateProducto();
        nuevoPago = new Pago();
        listaPagos = new ArrayList<>();
        listaProductos = new ArrayList<>();
        pagoAEditar = new Pago();
    }

    @PostConstruct
    public void init() {
        // La inicialización se moverá al getter de la lista para asegurar la ordenación.
    }

    // Usado por corte_caja.xhtml
    public void cambiarVista(String vista) {
        this.vistaActual = vista;
    }

    // Usado por corte_caja.xhtml
    public void guardarPago() {
        try {
            if (nuevoPago.getFecha() == null) {
                nuevoPago.setFecha(LocalDate.now());
            }

            nuevoPago.setIdProducto(delegateProducto.findProductoById(Integer.parseInt(idProductoString)));
            nuevoPago.setCambioRegresado(nuevoPago.getMontoRecibido() - nuevoPago.getIdProducto().getCosto());

            if (nuevoPago.getCambioRegresado() < 0) {
                // Si el cambio a regresar resulta ser negativo, entonces no pagó lo suficiente el cliente.
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "El monto registrado no es suficiente para cubrir el costo.");
            } else {
                delegatePago.savePago(nuevoPago);
                // El getter de la lista se encargará de recargarla y ordenarla
                nuevoPago = new Pago();

                addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago registrado correctamente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el pago.");
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // LÓGICA DE ORDENACIÓN APLICADA (NUEVA)
    public void aplicarOrdenacion() {
        // Cargar todos los pagos y los productos
        List<Pago> pagos = delegatePago.obtenerTodosLosPagos();
        List<Producto> productos = delegateProducto.obtenerTodosLosProductos();

        // Definir el comparador basado en el criterio de ordenación
        Comparator<Pago> comparatorPago;
        switch (this.ordenacionPagosActual) {
            case "FECHA_MAS_RECIENTE":
                // Ordenar por fecha, descendente (más reciente)
                comparatorPago = Comparator.comparing(Pago::getFecha, Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case "FECHA_MAS_ANTIGUA":
                // Ordenar por fecha, ascendente (más antigua)
                comparatorPago = Comparator.comparing(Pago::getFecha, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "MONTO_MAS_ALTO":
                // Ordenar por monto, descendente (más alto)
                comparatorPago = Comparator.comparing(Pago::getMontoFinal, Comparator.reverseOrder());
                break;
            case "MONTO_MAS_BAJO":
                // Ordenar por monto, ascendente (más bajo)
                comparatorPago = Comparator.comparing(Pago::getMontoFinal);
                break;
            case "ID_ASC":
                // Ordenar por ID, ascendente (registro más antiguo)
                comparatorPago = Comparator.comparing(Pago::getId);
                break;
            case "ID_DESC":
            default:
                // Ordenar por ID, descendente (registro más reciente - por defecto)
                comparatorPago = Comparator.comparing(Pago::getId, Comparator.reverseOrder());
                break;
        }

        Comparator<Producto> comparatorProducto;
        switch (this.ordenacionProductosActual) {
            case "COSTO_MAS_ALTO":
                // Ordenar por costo, descendente (más alto)
                comparatorProducto = Comparator.comparing(Producto::getCosto, Comparator.reverseOrder());
                break;
            case "COSTO_MAS_BAJO":
                // Ordenar por costo, ascendente (más bajo)
                comparatorProducto = Comparator.comparing(Producto::getCosto);
                break;
            case "ALFABETICO":
                // Ordenar por órden alfabético
                comparatorProducto = Comparator.comparing(Producto::getConcepto);
                break;
            case "ALFABETICO_INVERSO":
                // Ordenar por órden alfabético (inverso)
                comparatorProducto = Comparator.comparing(Producto::getConcepto, Comparator.reverseOrder());
                break;
            case "ID_ASC":
                // Ordenar por ID, ascendente (registro más antiguo)
                comparatorProducto = Comparator.comparing(Producto::getId);
                break;
            case "ID_DESC":
            default:
                // Ordenar por ID, descendente (registro más reciente - por defecto)
                comparatorProducto = Comparator.comparing(Producto::getId, Comparator.reverseOrder());
                break;
        }

        // Aplicar ordenación
        if (pagos != null) {
            pagos.sort(comparatorPago);
        }

        if (productos != null) {
            productos.sort(comparatorProducto);
        }

        this.listaPagos = pagos;
        this.listaProductos = productos;
    }

    // Método para manejar el cambio de ordenación en la vista (NUEVO)
    public void onSortChange() {
        // Esto se llama con p:ajax y actualiza la lista con el nuevo criterio.
        aplicarOrdenacion();
    }


    // GETTERS Y SETTERS

    public Pago getNuevoPago() {
        return nuevoPago;
    }

    public void setNuevoPago(Pago nuevoPago) {
        this.nuevoPago = nuevoPago;
    }

    public List<Pago> getListaPagos() {
        // Modificado para usar la lógica de ordenación
        aplicarOrdenacion();
        return this.listaPagos;
    }

    public List<Producto> getListaProductos() {
        // Modificado para usar la lógica de ordenación
        aplicarOrdenacion();
        return this.listaProductos;
    }

    public String getVistaActual() {
        return vistaActual;
    }

    public void setVistaActual(String vistaActual) {
        this.vistaActual = vistaActual;
    }

    public void setListaPagos(List<Pago> listaPagos) {
        this.listaPagos = listaPagos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public Pago getPagoAEditar() { return pagoAEditar; }

    public void setPagoAEditar(Pago pagoAEditar) { this.pagoAEditar = pagoAEditar; }

    // GETTER Y SETTER QUE FALTABAN Y CAUSABAN EL ERROR
    public String getOrdenacionPagosActual() {
        return ordenacionPagosActual;
    }

    public void setOrdenacionPagosActual(String ordenacionPagosActual) {
        this.ordenacionPagosActual = ordenacionPagosActual;
    }

    public String getOrdenacionProductosActual() {
        return ordenacionProductosActual;
    }

    public void setOrdenacionProductosActual(String ordenacionProductosActual) {
        this.ordenacionProductosActual = ordenacionProductosActual;
    }

    public String getIdProductoString() {
        return idProductoString;
    }

    public void setIdProductoString(String idProductoString) {
        this.idProductoString = idProductoString;
    }

    // Usado por corte_caja.xhtml para mostrar info del producto seleccionado
    public String getInfoProducto() {
        if (idProductoString.isBlank()) {
            infoProducto = "Esperando valor...";
        } else {
            Producto productoSeleccionado = delegateProducto.findProductoById(Integer.parseInt(idProductoString));
            infoProducto = productoSeleccionado.getConcepto() +": $"+ productoSeleccionado.getCosto();
        }
        return infoProducto;
    }

    public void setInfoProducto(String infoProducto) {
        this.infoProducto = infoProducto;
    }
}