package it.unina.registroelettronico.Database;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class JpaUtil {

    private static JpaUtil instance;

    private EntityManagerFactory emf;

    private JpaUtil() {
        emf = Persistence.createEntityManagerFactory("SistemaRegistroElettronico");

        //per chiamare automaticamente il metodo chiudi() alla chiusura dell'applicazione
        Runtime.getRuntime().addShutdownHook(new Thread(this::chiudi));
    }

    public static JpaUtil getInstance() {
        if (instance == null) {
            instance = new JpaUtil();
        }

        return instance;
    }


    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    public void chiudi() {
        emf.close();
    }
}
