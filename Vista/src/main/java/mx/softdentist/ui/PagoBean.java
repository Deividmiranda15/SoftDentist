package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import mx.softdentist.dao.ProductoDAO;
import mx.softdentist.delegate.DelegatePago;
import mx.softdentist.delegate.DelegatePagoTieneProducto;
import mx.softdentist.delegate.DelegateProducto;
import mx.softdentist.entidad.Pago;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import mx.softdentist.entidad.PagoTieneProducto;
import mx.softdentist.entidad.Producto;
import mx.softdentist.integration.ServiceLocator;

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
    private ProductoDAO productoDAO;
    private DelegatePago delegatePago;
    private DelegateProducto delegateProducto;
    private DelegatePagoTieneProducto delegatePagoTieneProducto;
    private Pago pagoAEditar;
    private String vistaActual = "ALTA";
    // PROPIEDAD REQUERIDA QUE FALTABA (O NO SE COMPILÓ)
    private String ordenacionPagosActual = "ID_DESC"; // Por defecto ID descendente (más nuevo)
    private String ordenacionProductosActual = "ID_ASC"; // Por defecto órden alfabético
    private String idProductoString = "";
    private String infoProducto = "";
    private String infoCambioADar = "";
    private Producto productoSeleccionado;  // Usado para evitar redundancias en getInfo
    private String cantidadSeleccionadoString = "";
    private List<Producto> productosAgregados;  // Usado para guardar los productos que forman parte del pago a registrar. (Es decir, se pagará por estos productos con estas cantidades.)
    private List<Integer> productosAgregadosCantidades; // Usado como las cantidades para cada producto agregado.

    public PagoBean() {
        System.out.println("DEBUG, INICIA PAGOBEAN");
        productoDAO = ServiceLocator.getInstanceProductoDAO();
        delegatePago = new DelegatePago();  // No estoy seguro si se debería crear el DelegatePago directamente o si estaría mejor crear una clase ServiceDelegateLocator.
        delegateProducto = new DelegateProducto();
        delegatePagoTieneProducto = new DelegatePagoTieneProducto();
        nuevoPago = new Pago();
        listaPagos = new ArrayList<>();
        listaProductos = new ArrayList<>();
        pagoAEditar = new Pago();
        productosAgregados = new ArrayList<>();
        productosAgregadosCantidades = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        System.out.println("DEBUG, INICIA POSTCONSTRUCT PAGOBEAN");
        // La inicialización se moverá al getter de la lista para asegurar la ordenación.
        try {
            aplicarOrdenacion();
//            listaProductos = productoDAO.obtenerTodos();
        } catch (Exception e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
            listaProductos = new ArrayList<>();
        }
        System.out.println("DEBUG: "+ listaProductos.getLast().getConcepto());
    }

    public void inicializarProductosSelectItems() {
        listaProductos = delegateProducto.obtenerTodosLosProductos();
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

            // Primero revisamos que el cambio a regresar no sea negativo.
            // El cambio a regresar debe de estar actualizado con cada registro de productos a la compra.
            if (nuevoPago.getCambioRegresado() < 0) {
                // Si el cambio a regresar resulta ser negativo, entonces no pagó lo suficiente el cliente.
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "El monto registrado no es suficiente para cubrir el costo.");
            } else {
                // Si ha pagado suficiente. Hay que guardar el pago...
                delegatePago.savePago(nuevoPago);
                // Hay que obtener la id del pago que acabamos de hacer...
                int idPago = nuevoPago.getId();
                // ...y usamos eso para guardar todos los productos que forman parte de ese pago.
                for (int i = 0; i < productosAgregados.toArray().length; i++) {
                    // Es necesario usar bucle "for" ya que el índice se usará para ubicar las cantidades asociadas con cada producto agregado.
                    guardarProductosSeleccionados(nuevoPago, productosAgregados.get(i), productosAgregadosCantidades.get(i));
                }
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

    // Método para agregar el producto seleccionado a la lista de productos agregados
    public void agregarProductoSeleccionado() {
        System.out.println("DEBUG: CANTIDAD ACTUAL: "+ cantidadSeleccionadoString);
        productoSeleccionado = delegateProducto.findProductoById(Integer.parseInt(idProductoString));
        agregarProductoSeleccionado(productoSeleccionado, Integer.parseInt(cantidadSeleccionadoString));
    }

    public void agregarProductoSeleccionado(Producto productoAAgregar, int cantidadAAgregar) {
        // Primero revisamos que la cantidad ingresada no sea menor a 1. (El interfáz no debe permitir que sea menor a 1, pero por si acaso...)
        if (cantidadAAgregar < 1) {
            // Si es menor a 1, avisamos al usuario.
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cantidad a agregar debe ser mayor a 1.");
        } else {
            // Si es mayor a 1, revisamos que si se haya seleccionado un producto.
            if (delegateProducto.findProductoById(productoAAgregar.getId()) == null) {
                // No se ha seleccionado producto.
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "Selecciona un producto a agregar.");
            } else {
                // Si ya se seleccionó un producto válido, revisamos si ya se ha agregado ese producto.
                if (productosAgregados.contains(productoAAgregar)){
                    // Si ya existe, avisamos al usuario.
                    addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya se ha agregado ese producto a la lista.");
                } else {
                    // Si todavía no existe, agregamos el producto con la cantidad seleccionada y avisamos al usuario.
                    productosAgregados.add(productoAAgregar);
                    productosAgregadosCantidades.add(cantidadAAgregar);

                    actualizarCambioRegresado();

                    addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto agregado a la lista correctamente.");
                    System.out.println("DEBUG: PRODUCTO Y CANTIDAD: "+ productoAAgregar.getConcepto() + " | "+ cantidadAAgregar);
                }
            }
        }
    }

    // Método para remover el producto seleccionado a la lista de productos agregados
    public void removerProductoSeleccionado(Producto productoARemover) {
        // No debería de ser posible, pero por si acaso revisamos que si existe el producto a remover en la lista de productos agregados.
        if (!productosAgregados.contains(productoARemover)){
            // Si no existe, avisamos al usuario.
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha agregado ese producto a la lista.");
        } else {
            // Si ya existe, buscamos el índice del producto a remover y usamos ese índice para remover tanto el producto como su cantidad asociada.
            int indice = productosAgregados.indexOf(productoARemover);

            productosAgregados.remove(indice);
            productosAgregadosCantidades.remove(indice);

            actualizarCambioRegresado();

            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto removido de la lista correctamente.");
        }
    }

    public void guardarProductosSeleccionados(Pago pago, Producto productoAgregado, int productoAgregadoCantidad) {
        PagoTieneProducto ptpAGuardar = new PagoTieneProducto();    // Creamos nuevo PagoTieneProducto
        ptpAGuardar.setIdPago(pago);   // Asignamos el Pago del PTP con este Pago que se está haciendo
        ptpAGuardar.setIdProducto(productoAgregado);    // Asignamos el Producto del PTP con el Producto de la iteración actual de la lista
        ptpAGuardar.setCantidad(productoAgregadoCantidad);  // Asignamos la cantidad del producto del PTP con la cantidad de la iteración actual de la lista
        delegatePagoTieneProducto.savePagoTieneProducto(ptpAGuardar);
    }

    // Método para actualizar el costo total y valores asociados
    public void actualizarCambioRegresado() {
        // Iteramos por las listas de productos agregados y sus cantidades para obtener una suma del costo total.
        float costoTotal = 0;
        for (int i = 0; i < productosAgregados.toArray().length; i++) {
            costoTotal += (productosAgregados.get(i).getCosto() * productosAgregadosCantidades.get(i));
        }

        // Una vez tenemos eso, usamos el costo total para calcular el cambio a regresar basado en el monto recibido.
        nuevoPago.setCambioRegresado(nuevoPago.getMontoRecibido() - costoTotal);
    }

    // LÓGICA DE ORDENACIÓN APLICADA
    public void aplicarOrdenacion() {
        // Cargar todos los pagos y los productos
        List<Pago> pagos = delegatePago.obtenerTodosLosPagos();
        List<Producto> productos;// = delegateProducto.obtenerTodosLosProductos();
        try {
            productos = productoDAO.obtenerTodos();
        } catch (Exception e) {
            System.out.println("Error al cargar productos desde DAO: " + e.getMessage());
            productos = new ArrayList<>();
        }

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

        // Si no son nulos las variables del método,  usamos lo que sacamos. De otro modo creamos arraylist nuevo.
        this.listaPagos = pagos != null ? pagos : new ArrayList<>();
        this.listaProductos = productos != null ? productos : new ArrayList<>();
    }

    // Método para manejar el cambio de ordenación en la vista
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
//        aplicarOrdenacion();
        return this.listaPagos;
    }

    public List<Producto> getListaProductos() {
        // Modificado para usar la lógica de ordenación
//        aplicarOrdenacion();
        return this.listaProductos;
    }

    public String getVistaActual() {
        return vistaActual;
    }

    public List<Producto> getProductosAgregados() {
        return this.productosAgregados;
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

    public void setProductosAgregados(List<Producto> productosAgregados) {
        this.productosAgregados = productosAgregados;
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
            productoSeleccionado = delegateProducto.findProductoById(Integer.parseInt(idProductoString));
            if (productoSeleccionado == null) {
                infoProducto = "ID inválido.";
            } else {
                infoProducto = productoSeleccionado.getConcepto() +": $"+ productoSeleccionado.getCosto();
            }
        }
        return infoProducto;
    }

    public void setInfoProducto(String infoProducto) {
        this.infoProducto = infoProducto;
    }

    // Usado por corte_caja.xhtml para mostrar la cantidad de cambio a dar
    public String getInfoCambioADar() {
        System.out.println("DEBUG: ID Producto actual: "+ idProductoString);
        System.out.println("DEBUG: Cantidad actual: "+ cantidadSeleccionadoString);
        if (productosAgregados.isEmpty()) {
            infoCambioADar = "Agrega un producto.";
        } else {
            if (nuevoPago.getMontoRecibido() == null) {
                infoCambioADar = "Ingrese un monto.";
            } else {
                float costoTotal = 0;
                for (int i = 0; i < productosAgregados.toArray().length; i++) {
                    costoTotal += productosAgregados.get(i).getCosto() * productosAgregadosCantidades.get(i);
                }

                nuevoPago.setCambioRegresado(nuevoPago.getMontoRecibido() - costoTotal);
                if (nuevoPago.getCambioRegresado() < 0) {
                    infoCambioADar = "El monto ingresado no es suficiente para cubrir el costo del servicio seleccionado.";
                } else {
                    infoCambioADar = "Cambio a dar al cliente: $" + nuevoPago.getCambioRegresado();
                }
            }
        }
        return infoCambioADar;
    }

    public void setInfoCambioADar(String infoCambioADar) {
        this.infoCambioADar = infoCambioADar;
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public String getcantidadSeleccionadoString() {
        return cantidadSeleccionadoString;
    }

    public void setcantidadSeleccionadoString(String cantidadSeleccionadoString) {
        this.cantidadSeleccionadoString = cantidadSeleccionadoString;
    }

    public List<Integer> getProductosAgregadosCantidades() {
        return productosAgregadosCantidades;
    }

    public void setProductosAgregadosCantidades(List<Integer> productosAgregadosCantidades) {
        this.productosAgregadosCantidades = productosAgregadosCantidades;
    }

    public DelegatePago getDelegatePago() {
        return delegatePago;
    }

    public void setDelegatePago(DelegatePago delegatePago) {
        this.delegatePago = delegatePago;
    }

    public DelegateProducto getDelegateProducto() {
        return delegateProducto;
    }

    public void setDelegateProducto(DelegateProducto delegateProducto) {
        this.delegateProducto = delegateProducto;
    }
}