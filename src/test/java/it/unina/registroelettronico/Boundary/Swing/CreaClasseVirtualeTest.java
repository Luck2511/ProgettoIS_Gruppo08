package it.unina.registroelettronico.Boundary.Swing;

import it.unina.registroelettronico.Controller.ControllerClassi;
import it.unina.registroelettronico.Controller.ControllerUtenti;
import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.Database.JpaUtil;
import it.unina.registroelettronico.Entity.ClasseVirtuale;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite di Test - Crea Classe Virtuale") //fa apparire in JUnit un nome leggibile
class CreaClasseVirtualeTest {

    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = JpaUtil.getInstance().getEntityManager();
        svuotaTabelle();
    }

    /**
     * Prima di ogni test svuota le tabelle applicative coinvolte.
     *
     * I test usano il database reale configurato nel persistence.xml
     */
    private void svuotaTabelle() {
        EntityTransaction tx = em.getTransaction();

        try {
            if (!tx.isActive()) {//se la transazione non è già attiva la attiviamo
                tx.begin();
            }

            // Prima vengono eliminate relazioni e entità figlie per rispettare le FK.
            em.createNativeQuery("DELETE FROM classe_studente").executeUpdate();
            em.createQuery("DELETE FROM Valutazione").executeUpdate();
            em.createQuery("DELETE FROM Lezione").executeUpdate();
            em.createQuery("DELETE FROM Compito").executeUpdate();
            em.createQuery("DELETE FROM ClasseVirtuale").executeUpdate();
            em.createQuery("DELETE FROM Registro").executeUpdate();
            em.createQuery("DELETE FROM Docente").executeUpdate();
            em.createQuery("DELETE FROM Studente").executeUpdate();
            em.createQuery("DELETE FROM Utente").executeUpdate();

            /*
                createNativeQuery() usa SQL nativo, dobbiamo usare questo perché classe_studente è solo una tabella del database
                Invece, createQuery() usa JPQL in quanto per esempio Valutazione è il nome dell'Entity Java e non della tabella del db
             */


            tx.commit();
            em.clear();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(
                    "Errore durante la pulizia del DB di test: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Crea il docente richiesto dalle precondizioni di tutti test case. Serve a non ripetere in ciascun test tutta la preparazione del docente.
     * La registrazione attraversa il normale ControllerUtenti e persiste il docente.
     */
    private DocenteDTO creaDocenteAutenticato(String suffisso) {
        DocenteDTO docente = new DocenteDTO(
                "Docente",
                "Test",
                "docente." + suffisso + "@test.unina.it",
                "password-test"
        );

        assertTrue(ControllerUtenti.getInstance().registraDocente(docente));
        //Mettiamo assertTrue() perché è una precondizione del test; restituisse false, il caso di test non avrebbe più senso: penseremmo di stare testando
        //la creazione di una classe con docente esistente, ma il docente non sarebbe stato creato.
        return docente;
    }

    /**
     * Recupera dal DB le classi del docente. Questo rappresenta il nostro oracolo sul database
     * Riceve l'email del docente e recupera tutte le classi realmente persistite per lui.
     */
    private List<ClasseVirtuale> recuperaClassiDocente(String emailDocente) {
        em.clear();

        return em.createQuery(
                        "SELECT c FROM ClasseVirtuale c " +
                                "WHERE c.docente.indirizzoEmailIstituzionale = :email",
                        ClasseVirtuale.class
                )
                .setParameter("email", emailDocente)
                .getResultList();
    }

    //CASO DI SUCCESSO

    @Test
    @DisplayName("TC1: Nome della classe valido")
    void creaClasseVirtuale_NomeValido_CreaEPersisteClasse() {
        // Arrange: docente autenticato e nome non ancora utilizzato.
        //Precondizione: il docente è autenticato
        DocenteDTO docente = creaDocenteAutenticato("tc1");
        SchermataDocente schermataDocente = new SchermataDocente(docente);

        /*
         * I componenti Swing sono oggetti fittizi usati soltanto perché fanno parte
         * della firma del VERO metodo della Boundary. Non vengono testati graficamente.
         */
        JTextField txtNomeClasse = new JTextField("3A Informatica");
        //Avendo nel beforeEach cancellato le tabelle del database e avendo creato semplicemente il docente fittizio
        //siamo certi che tale docente non abbia questa classe tra le sue classi
        JLabel lblErrore = new JLabel();

        // Act: viene chiamato esattamente il metodo usato dall'applicazione.
        schermataDocente.confermaCreazioneClasse(
                null,//null come JDialog perché non vogliamo creare una finestra reale
                txtNomeClasse,
                lblErrore
        );

        // Assert: la nuova classe deve essere stata realmente persistita.
        List<ClasseVirtuale> classi =
                recuperaClassiDocente(docente.getIndirizzoEmailIstituzionale());

        //ci aspettiamo esattamente una classe tra quelle del docente
        assertEquals(1, classi.size());
        //Controlliamo che la classe salvata abbia esattamente il nome richiesto
        assertEquals("3A Informatica", classi.get(0).getNome());
        //Il sistema deve aver assegnato un codice.
        //Quindi non deve essere null.
        assertNotNull(classi.get(0).getCodice());

    }


    // CASI DI ERRORE


    @Test
    @DisplayName("TC2: Nome già utilizzato dallo stesso docente")
    void creaClasseVirtuale_NomeGiaUtilizzato_NonCreaNuovaClasse() throws Exception {
        // Arrange: il docente possiede già una classe con lo stesso nome.
        //Precondizione: il docente è autenticato
        DocenteDTO docente = creaDocenteAutenticato("tc2");

        //Precondizione: il docente possiede già una classe chiamata "3A Informatica"
        //assertTrue() perché se non riesco nemmeno a predisporre la classe preesistente, TC2 non è valido e deve fallire subito.
        InfoClasseDTO classePreesistente = new InfoClasseDTO("", "");
        assertTrue(
                ControllerClassi.getInstance().creaClasse(
                        "3A Informatica",
                        classePreesistente,
                        docente
                )
        );
        String codiceClassevecchia=classePreesistente.getCodice();

        SchermataDocente schermataDocente = new SchermataDocente(docente);
        JTextField txtNomeClasse = new JTextField("3A Informatica");
        JLabel lblErrore = new JLabel();

        // Act: il metodo reale intercetta ClasseEsistenteException e termina il flusso.
        schermataDocente.confermaCreazioneClasse(
                null,
                txtNomeClasse,
                lblErrore
        );

        /*
            Dentro il vero metodo:
            - il nome non è vuoto;
            - non supera 50 caratteri;
            - arriva a ControllerClassi.creaClasse(...);
            - il controller chiede al sistema se il nome è univoco;
            - Docente.verificaUnivocitaNome(...) trova già "3A Informatica";
            - viene lanciata ClasseEsistenteException;
            - SchermataDocente la intercetta;
            - scrive il messaggio sulla label;
            - esegue return.
            Noi non testiamo la label.
            Testiamo l'effetto funzionale.
         */



        // Assert: non verifichiamo la JLabel; controlliamo che il DB sia invariato.
        List<ClasseVirtuale> classi =
                recuperaClassiDocente(docente.getIndirizzoEmailIstituzionale());

        //Prima dell'Act c'era una classe.
        //Dopo l'Act deve essercene ancora una sola.
        assertEquals(1, classi.size());
        //Controlliamo che l'unica classe rimasta sia proprio quella preesistente
        assertEquals("3A Informatica", classi.get(0).getNome());
        assertEquals(codiceClassevecchia,classi.get(0).getCodice());
    }

    @Test
    @DisplayName("TC3: Nome troppo lungo (51 caratteri)")
    void creaClasseVirtuale_NomeTroppoLungo_NonCreaClasse() {
        //Precondizione: docente autenticato
        DocenteDTO docente = creaDocenteAutenticato("tc3");
        SchermataDocente schermataDocente = new SchermataDocente(docente);

        JTextField txtNomeClasse = new JTextField("A".repeat(51));
        JLabel lblErrore = new JLabel();

        // Act: il controllo di lunghezza è eseguito dal vero metodo della Boundary.
        schermataDocente.confermaCreazioneClasse(
                null,
                txtNomeClasse,
                lblErrore
        );

        //contiamo il numero di classi persistenti per il docente e ci assicuriamo sia 0
        assertEquals(
                0,
                recuperaClassiDocente(docente.getIndirizzoEmailIstituzionale()).size()
        );
    }

    @Test
    @DisplayName("TC4: Nome non inserito")
    void creaClasseVirtuale_NomeVuoto_NonCreaClasse() {
        //Precondizione: docente autenticato
        DocenteDTO docente = creaDocenteAutenticato("tc4");
        SchermataDocente schermataDocente = new SchermataDocente(docente);

        JTextField txtNomeClasse = new JTextField("");
        JLabel lblErrore = new JLabel();

        // Act: il controllo sul nome vuoto è eseguito dal vero metodo della Boundary.
        schermataDocente.confermaCreazioneClasse(
                null,
                txtNomeClasse,
                lblErrore
        );

        //contiamo il numero di classi persistenti per il docente e ci assicuriamo sia 0
        assertEquals(
                0,
                recuperaClassiDocente(docente.getIndirizzoEmailIstituzionale()).size()
        );
    }
}
