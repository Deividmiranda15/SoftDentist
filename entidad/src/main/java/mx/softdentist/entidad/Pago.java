package mx.softdentist.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "pago", schema = "softdentist")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "monto_recibido", nullable = false)
    private Float montoRecibido;

    @NotNull
    @Column(name = "cambio_regresado", nullable = false)
    private Float cambioRegresado;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto idProducto;


    public Float getMontoFinal(){ return montoRecibido - cambioRegresado;}


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(Float montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public Float getCambioRegresado() {
        return cambioRegresado;
    }

    public void setCambioRegresado(Float cambioRegresado) {
        this.cambioRegresado = cambioRegresado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

}