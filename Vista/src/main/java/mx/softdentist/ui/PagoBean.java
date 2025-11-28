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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named("pagoBean")
@ViewScoped
public class PagoBean implements Serializable {

    private Pago nuevoPago;
    private List<Pago> listaPagos;
    private PagoDAO pagoDAO;
    private Pago pagoAEditar;

    public PagoBean() {
        pagoDAO = ServiceLocator.getInstancePagoDAO();
        nuevoPago = new Pago();
        listaPagos = new ArrayList<>();
        pagoAEditar = new Pago();
    }

    @PostConstruct
    public void init() {
        listaPagos = pagoDAO.obtenerTodos();
    }

    public void guardarPago() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            nuevoPago.setId(nuevoPago.getCitas().getId());

            pagoDAO.save(nuevoPago);
            listaPagos = pagoDAO.obtenerTodos(); // refresca tabla
            nuevoPago = new Pago(); // limpia formulario

            //mensaje de exito
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Éxito", "Pago guardado"));

            // mensaje de exito
            addGlobalMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago registrado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();

            //menaje de error
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el pago"));
            // mensaje de fallo
            addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar al pago. Intente de nuevo.");
        }
    }
    public void cargarDatosParaEditar() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                // Usamos el DAO para buscar el pago
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
        // Este es para luego hacer la modificacion con un editarPago.xhtml
        return "editarPago.xhtml?faces-redirect=true&id=" + p.getId();
    }

    // Getters y setters
    public Pago getNuevoPago() {
        return nuevoPago;
    }

    public void setNuevoPago(Pago nuevoPago) {
        this.nuevoPago = nuevoPago;
    }

    public List<Pago> getListaPagos() {
        listaPagos = pagoDAO.obtenerTodos();    // Para no accidentalmente enviar resultados de busqueda
        return listaPagos;
    }

    public void setListaPagos(List<Pago> listaPagos) {
        this.listaPagos = listaPagos;
    }

    public Pago getPagoAEditar() { return pagoAEditar; }

    public void setPagoAEditar(Pago pagoAEditar) { this.pagoAEditar = pagoAEditar; }
}
