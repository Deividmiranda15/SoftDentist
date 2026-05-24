package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mx.softdentist.entidad.PagoTieneProducto;
import mx.softdentist.persistence.AbstractDAO;

import java.util.List;

public class PagoTieneProductoDAO extends AbstractDAO<PagoTieneProducto> {
    private final EntityManager entityManager;

    public PagoTieneProductoDAO(EntityManager em) {
        super(PagoTieneProducto.class);
        this.entityManager = em;
    }

    public List<PagoTieneProducto> obtenerTodos(){
        return entityManager
                .createQuery("SELECT ptp FROM PagoTieneProducto ptp", PagoTieneProducto.class)
                .getResultList();
    }

    public List<PagoTieneProducto> obtenerPorIdPago(int idPago){
        try {
            TypedQuery<PagoTieneProducto> query = getEntityManager().createQuery(
                    // TODO: Revisar si usar "idPago" causa problemas, cambiar a "id_pago" de ser necesario
                    // TODO aplica a obtenerPorIdPago, obtenerPorIdProducto y obtenerPorBothId
                    "SELECT ptp FROM PagoTieneProducto ptp WHERE ptp.idPago = :idPagoParam", PagoTieneProducto.class);
            query.setParameter("idPagoParam", idPago);
            return query.getResultList();
        } catch (NoResultException e) {
            // Si no se encuentra, devuelve null.
            return null;
        }
    }

    public List<PagoTieneProducto> obtenerPorIdProducto(int idProducto){
        try {
            TypedQuery<PagoTieneProducto> query = getEntityManager().createQuery(
                    "SELECT ptp FROM PagoTieneProducto ptp WHERE ptp.idProducto = :idProductoParam", PagoTieneProducto.class);
            query.setParameter("idProductoParam", idProducto);
            return query.getResultList();
        } catch (NoResultException e) {
            // Si no se encuentra, devuelve null.
            return null;
        }
    }

    public PagoTieneProducto obtenerPorBothId(int idPago, int idProducto){
        try {
            TypedQuery<PagoTieneProducto> query = getEntityManager().createQuery(
                    "SELECT ptp FROM PagoTieneProducto ptp WHERE ptp.idPago = :idPagoParam AND ptp.idProducto = :idProductoParam", PagoTieneProducto.class);
            query.setParameter("idPagoParam", idPago);
            query.setParameter("idProductoParam", idProducto);
            return query.getSingleResult();
        } catch (NoResultException e) {
            // Si no se encuentra, devuelve null.
            return null;
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
