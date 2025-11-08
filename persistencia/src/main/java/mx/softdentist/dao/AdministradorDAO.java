package mx.softdentist.dao;

import jakarta.persistence.EntityManager;
import mx.softdentist.persistence.AbstractDAO;
import mx.softdentist.entidad.Administrador;
import jakarta.persistence.NoResultException;

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

    public Administrador findByCorreo(String correo) {
        try {
            // Enfoque alternativo para evitar el error de tipos del IDE
            jakarta.persistence.Query query = entityManager.createQuery(
                    "SELECT a FROM Administrador a WHERE a.correo = :correoParam");

            // Asignamos el parámetro
            query.setParameter("correoParam", correo);

            // Ejecutamos y hacemos un "cast" manual del resultado al tipo Administrador
            return (Administrador) query.getSingleResult();

        } catch (NoResultException e) {

            return null;
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
