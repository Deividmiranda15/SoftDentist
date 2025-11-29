package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.entidad.Pago;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.persistence.HibernateUtil;
import java.util.List;

public class PagoDAO extends AbstractDAO<Pago> {

    public PagoDAO() {
        super(Pago.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return HibernateUtil.getEntityManager();
    }


    //Obtiene todos los registros de la tabla pago.

    public List<Pago> obtenerTodos() {
        try {
            // Usamos el EntityManager que acabamos de implementar
            return getEntityManager()
                    .createQuery("SELECT p FROM Pago p", Pago.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}