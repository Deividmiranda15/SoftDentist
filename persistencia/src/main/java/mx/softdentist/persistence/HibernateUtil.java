package mx.softdentist.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());
    private static EntityManagerFactory entityManagerFactory;

    static {
        initEntityManagerFactory();
    }

    private static void initEntityManagerFactory() {
        try {
            logger.info("Inicializando EntityManagerFactory para persistencePU...");

            // Verificar que el driver esté disponible
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                logger.info("Driver MySQL cargado correctamente");
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "ERROR: Driver MySQL no encontrado", e);
                throw new ExceptionInInitializerError("Driver MySQL no encontrado: " + e.getMessage());
            }

            // Usar EXACTAMENTE el mismo nombre que en persistence.xml
            entityManagerFactory = Persistence.createEntityManagerFactory("persistencePU");
            logger.info("EntityManagerFactory creado exitosamente para: persistencePU");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR CRÍTICO al crear EntityManagerFactory: " + e.getMessage(), e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            initEntityManagerFactory();
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        try {
            return getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al crear EntityManager", e);
            throw new RuntimeException("No se pudo crear EntityManager", e);
        }
    }
}