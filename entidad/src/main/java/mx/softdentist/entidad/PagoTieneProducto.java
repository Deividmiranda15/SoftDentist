package mx.softdentist.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pagotieneproducto", schema = "softdentist")
public class PagoTieneProducto {
    @EmbeddedId
    private PagoTieneProductoId id = new PagoTieneProductoId(); // Como no tiene "ID propio", se requiere instanciar uno al construir la clase.

    @MapsId("idProducto")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto idProducto;

    @MapsId("idPago")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago idPago;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public PagoTieneProductoId getId() {
        return id;
    }

    public void setId(PagoTieneProductoId id) {
        this.id = id;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    public Pago getIdPago() {
        return idPago;
    }

    public void setIdPago(Pago idPago) {
        this.idPago = idPago;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}