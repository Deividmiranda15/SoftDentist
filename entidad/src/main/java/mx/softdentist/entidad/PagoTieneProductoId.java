package mx.softdentist.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PagoTieneProductoId implements Serializable {
    private static final long serialVersionUID = 7711916124943723079L;
    @NotNull
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @NotNull
    @Column(name = "id_pago", nullable = false)
    private Integer idPago;

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PagoTieneProductoId entity = (PagoTieneProductoId) o;
        return Objects.equals(this.idPago, entity.idPago) &&
                Objects.equals(this.idProducto, entity.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPago, idProducto);
    }

}