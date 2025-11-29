package mx.softdentist.facade;

import mx.softdentist.dao.PagoDAO;
import mx.softdentist.entidad.Pago;
import java.util.List;

public class FacadePago {

    private final PagoDAO pagoDAO = new PagoDAO();

    public List<Pago> obtenerTodos() {
        return pagoDAO.obtenerTodos();
    }

    // Dejamos el método guardar comentado o presente pero sin uso en la vista US2
    public void guardar(Pago pago) {
        pagoDAO.save(pago);
    }
}