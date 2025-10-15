package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Administrador;

import java.util.List;

// Rama Desarrollo
public class AdministradorDAO extends AbstractDAO<Administrador> {
    private final EntityManager entityManager;

    public AdministradorDAO(EntityManager em) {
        super(Administrador.class);
        this.entityManager = em;
    }

    public List<Administrador> obtenerTodos(){
        return entityManager
                .createQuery("SELECT a FROM Administrador a", Administrador.class)
                .getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
