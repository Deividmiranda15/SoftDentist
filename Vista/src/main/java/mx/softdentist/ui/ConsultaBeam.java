package mx.softdentist.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.softdentist.delegate.DelegateCita;
import mx.softdentist.entidad.Cita;
import java.io.Serializable;
import java.util.List;

@Named("citaConsultaBean")
@ViewScoped

public class ConsultaBeam implements Serializable {

    private List<Cita> listaCitas;
    private DelegateCita delegateCita;

    @PostConstruct
    public void init() {
        delegateCita = new DelegateCita();
        this.listaCitas = delegateCita.obtenerTodasCitas();
    }

    public List<Cita> getListaCitas() {
        return listaCitas;
    }
}
