package mx.softdentist.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp; // Timestamp para guardar fecha y hora exactas

@Entity
@Table(name = "mensaje")
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    // 2000 caracteres.
    @Column(name = "contenido", nullable = false, length = 2000)
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private Timestamp fechaEnvio;

    @Column(name = "leido", nullable = false)
    private boolean leido;

    // Quien envía
    @Column(name = "id_emisor", nullable = false)
    private Long idEmisor;

    // si es paciente o empleado
    @Column(name = "tipo_emisor", length = 10, nullable = false)
    private String tipoEmisor;

    // Quien recibe
    @Column(name = "id_receptor", nullable = false)
    private Long idReceptor;

    // Si es paciente o empleado
    @Column(name = "tipo_receptor", length = 10, nullable = false)
    private String tipoReceptor;

    public Mensaje() {
    }

    // Getters y Setters

    public Long getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(Long idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Timestamp getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Timestamp fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public Long getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(Long idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getTipoEmisor() {
        return tipoEmisor;
    }

    public void setTipoEmisor(String tipoEmisor) {
        this.tipoEmisor = tipoEmisor;
    }

    public Long getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(Long idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getTipoReceptor() {
        return tipoReceptor;
    }

    public void setTipoReceptor(String tipoReceptor) {
        this.tipoReceptor = tipoReceptor;
    }
}