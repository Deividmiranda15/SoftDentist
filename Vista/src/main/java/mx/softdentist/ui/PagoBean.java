package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import mx.softdentist.dao.PagoDAO;
import mx.softdentist.entidad.Pago;
import mx.softdentist.integration.ServiceLocator;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Comparator; // Importación necesaria para el sort

@Named("pagoBean")
@ViewScoped
public class PagoBean implements Serializable {

    private Pago nuevoPago;
    private List<Pago> listaPagos;
    private PagoDAO pagoDAO;
    private Pago pagoAEditar;
    private String vistaActual = "CONSULTA";
    // PROPIEDAD REQUERIDA QUE FALTABA (O NO SE COMPILÓ)
    private String ordenacionActual = "ID_DESC"; // Por defecto ID descendente (más nuevo)

    public PagoBean() {
        pagoDAO = ServiceLocator.getInstancePagoDAO();
        nuevoPago = new Pago();
        listaPagos = new ArrayList<>();
        pagoAEditar = new Pago();
    }

    @PostConstruct
    public void init() {
        // La inicialización se moverá al getter de la lista para asegurar la ordenación.
    }

    public void cambiarVista(String vista) {
        this.vistaActual = vista;
    }

    public void guardarPago() {
        try {
            if (nuevoPago.getFecha() == null) {
                nuevoPago.setFecha(LocalDate.now());
            }

            pagoDAO.save(nuevoPago);
            // El getter de la lista se encargará de recargarla y ordenarla
            nuevoPago = new Pago();

            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago registrado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el pago.");
        }
    }

    public void cargarDatosParaEditar() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                this.pagoAEditar = pagoDAO.find(id).orElse(new Pago());
            } catch (NumberFormatException e) {
                addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "ID de pago inválido.");
            }
        }
    }

    public void actualizarPago() {
        try {
            pagoDAO.update(pagoAEditar);
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago actualizado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar.");
        }
    }

    private void addGlobalMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public String irAEditar(Pago p) {
        return "editarPago.xhtml?faces-redirect=true&id=" + p.getId();
    }

    // LÓGICA DE ORDENACIÓN APLICADA (NUEVA)
    public void aplicarOrdenacion() {
        // Cargar todos los pagos
        List<Pago> pagos = pagoDAO.obtenerTodos();

        // Definir el comparador basado en el criterio de ordenación
        Comparator<Pago> comparator;
        switch (this.ordenacionActual) {
            case "FECHA_MAS_RECIENTE":
                // Ordenar por fecha, descendente (más reciente)
                comparator = Comparator.comparing(Pago::getFecha, Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case "FECHA_MAS_ANTIGUA":
                // Ordenar por fecha, ascendente (más antigua)
                comparator = Comparator.comparing(Pago::getFecha, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "MONTO_MAS_ALTO":
                // Ordenar por monto, descendente (más alto)
                comparator = Comparator.comparing(Pago::getMonto, Comparator.reverseOrder());
                break;
            case "MONTO_MAS_BAJO":
                // Ordenar por monto, ascendente (más bajo)
                comparator = Comparator.comparing(Pago::getMonto);
                break;
            case "ID_ASC":
                // Ordenar por ID, ascendente (registro más antiguo)
                comparator = Comparator.comparing(Pago::getId);
                break;
            case "ID_DESC":
            default:
                // Ordenar por ID, descendente (registro más reciente - por defecto)
                comparator = Comparator.comparing(Pago::getId, Comparator.reverseOrder());
                break;
        }

        // Aplicar ordenación
        if (pagos != null) {
            pagos.sort(comparator);
        }

        this.listaPagos = pagos;
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

    public String getVistaActual() {
        return vistaActual;
    }

    public void setVistaActual(String vistaActual) {
        this.vistaActual = vistaActual;
    }

    public void setListaPagos(List<Pago> listaPagos) {
        this.listaPagos = listaPagos;
    }

    public Pago getPagoAEditar() { return pagoAEditar; }

    public void setPagoAEditar(Pago pagoAEditar) { this.pagoAEditar = pagoAEditar; }

    // GETTER Y SETTER QUE FALTABAN Y CAUSABAN EL ERROR
    public String getOrdenacionActual() {
        return ordenacionActual;
    }

    public void setOrdenacionActual(String ordenacionActual) {
        this.ordenacionActual = ordenacionActual;
    }
}