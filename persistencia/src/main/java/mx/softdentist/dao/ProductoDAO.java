package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.entidad.Producto;
import mx.softdentist.persistence.AbstractDAO;

import java.util.List;

public class ProductoDAO extends AbstractDAO<Producto> {
    private final EntityManager entityManager;

    public ProductoDAO(EntityManager em) {
        super(Producto.class);
        this.entityManager = em;
    }

    public List<Producto> obtenerTodos(){
        return entityManager
                .createQuery("SELECT p FROM Producto p", Producto.class)
                .getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}