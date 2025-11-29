package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.delegate.DelegatePago;
import mx.softdentist.entidad.Pago;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("pagoBean")
@ViewScoped
public class PagoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Lista para almacenar los resultados de la consulta
    private List<Pago> listaPagos;

    // Objeto para manejar la selección en la tabla (preparando para el futuro)
    private Pago pagoSeleccionado;

    // Delegado de negocio
    private final DelegatePago delegatePago = new DelegatePago();

    @PostConstruct
    public void init() {
        // Inicializamos la lista vacía por seguridad
        listaPagos = new ArrayList<>();
        // Cargamos los datos inmediatamente al entrar a la página
        consultarPagos();
    }

    //Método para consultar datos

    public void consultarPagos() {
        try {
            // Llama a la capa de negocio que a su vez llama al DAO
            this.listaPagos = delegatePago.obtenerTodos();

            if (this.listaPagos == null) {
                this.listaPagos = new ArrayList<>();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Consulta", "No se pudo cargar la lista de pagos."));
            e.printStackTrace();
        }
    }

    // --- Getters y Setters ---

    public List<Pago> getListaPagos() {
        return listaPagos;
    }

    public void setListaPagos(List<Pago> listaPagos) {
        this.listaPagos = listaPagos;
    }

    public Pago getPagoSeleccionado() {
        return pagoSeleccionado;
    }

    public void setPagoSeleccionado(Pago pagoSeleccionado) {
        this.pagoSeleccionado = pagoSeleccionado;
    }
}