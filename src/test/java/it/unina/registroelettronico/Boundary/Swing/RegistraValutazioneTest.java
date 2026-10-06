package it.unina.registroelettronico.Boundary.Swing;

import it.unina.registroelettronico.Controller.ControllerClassi;
import it.unina.registroelettronico.Controller.ControllerUtenti;
import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;
import it.unina.registroelettronico.Database.JpaUtil;
import it.unina.registroelettronico.Entity.SistemaRegistroElettronico;
import it.unina.registroelettronico.Entity.TipologiaValutazione;
import it.unina.registroelettronico.Entity.Valutazione;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite di Test - Registra Valutazione")
class RegistraValutazioneTest {

    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = JpaUtil.getInstance().getEntityManager();
        svuotaTabelle();
    }

    /**
     * Prima di ogni test elimina i dati applicativi dal database reale.
     */
    private void svuotaTabelle() {
        EntityTransaction tx = em.getTransaction();

        try {
            if (!tx.isActive()) {
                tx.begin();
            }

            em.createNativeQuery("DELETE FROM classe_studente").executeUpdate();
            em.createQuery("DELETE FROM Valutazione").executeUpdate();
            em.createQuery("DELETE FROM Lezione").executeUpdate();
            em.createQuery("DELETE FROM Compito").executeUpdate();
            em.createQuery("DELETE FROM ClasseVirtuale").executeUpdate();
            em.createQuery("DELETE FROM Registro").executeUpdate();
            em.createQuery("DELETE FROM Docente").executeUpdate();
            em.createQuery("DELETE FROM Studente").executeUpdate();
            em.createQuery("DELETE FROM Utente").executeUpdate();

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
     * Predispone le precondizioni comuni della test suite:
     * docente, studente, classe virtuale e iscrizione dello studente alla classe.
     */
    private TestContext preparaContesto(String suffisso, String codiceClasse) throws Exception {
        //Per la precondizione docente autenticato
        DocenteDTO docente = new DocenteDTO(
                "Docente",
                "Test",
                "docente." + suffisso + "@test.unina.it",
                "password-test"
        );

        //Per la precondizione per cui la classe deve avere almeno uno studente (Studente A)
        //creo qui lo studente
        StudenteDTO studente = new StudenteDTO(
                "Studente",
                "A",
                "studente." + suffisso + "@test.unina.it",
                "password-test"
        );

        //Questi assertTrue() servono per dire:
        //se non riesco a creare la precondizione, cioè a registrare il docente e lo studente,
        //interrompi subito il test.
        assertTrue(ControllerUtenti.getInstance().registraDocente(docente));
        assertTrue(ControllerUtenti.getInstance().registraStudente(studente));

        //Questo assertTrue() serve per bloccare il test se non riesce a creare una classe
        assertTrue(
                SistemaRegistroElettronico.getInstance().memorizzaClasse(
                        "Classe valutazioni " + suffisso,
                        codiceClasse,
                        docente.getIndirizzoEmailIstituzionale()
                )
        );

        InfoClasseDTO classe = new InfoClasseDTO("", "");
        //Qui iscriviamo lo studente alla classe.
        assertTrue(
                ControllerClassi.getInstance().iscrizioneStudenteTramiteCodiceClasse(
                        codiceClasse,
                        classe,
                        studente
                )
        );

        //Alla fine quindi abbiamo le seguenti precondizioni realizzate:
        /*
        Docente esistente
        Classe virtuale (e registro associato) esistente
        Studente esistente
        Studente iscritto alla classe
        */

        //Ora restituiamo i due oggetti che serviranno nel test, la classe e lo studente ad essa iscritto come oggetti DTO
        //che sono quelli con cui lavorano le classi del boundary
        return new TestContext(classe, studente);//TestContext è un record dichiarato alla fine della classe
    }

    /** Recupera dal DB reale le valutazioni associate allo studente. */
    private List<Valutazione> recuperaValutazioniStudente(String emailStudente) {
        em.clear();

        return em.createQuery(
                        "SELECT v FROM Valutazione v " +
                                "WHERE v.studente.indirizzoEmailIstituzionale = :email",
                        Valutazione.class
                )
                .setParameter("email", emailStudente)
                .getResultList();
    }

    /** Conta le valutazioni persistite per lo studente. */
    private long numeroValutazioniStudente(String emailStudente) {
        em.clear();

        return em.createQuery(
                        "SELECT COUNT(v) FROM Valutazione v " +
                                "WHERE v.studente.indirizzoEmailIstituzionale = :email",
                        Long.class
                )
                .setParameter("email", emailStudente)
                .getSingleResult();
    }

    /**
     * Crea la Boundary e disabilita esclusivamente i JOptionPane.
     * Il metodo registraValutazione() che verrà eseguito rimane quello originale, void e senza argomenti.
     */
    private SchermataRegistraValutazione creaSchermata(TestContext ctx) {
        SchermataRegistraValutazione schermata =
                new SchermataRegistraValutazione(ctx.classe());

        schermata.setMostraMessaggi(false);
        return schermata;
    }

    //CASO DI SUCCESSO

    @Test
    @DisplayName("TC1: Tutti gli input validi")
    void registraValutazione_InputValidi_PersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc1", "CLS-RV0001");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        /*
         * Arrange: i setter package-private impostano i componenti realmente letti
         * dal metodo registraValutazione(). Non viene così usata la reflection.
         */
        schermata.setStudentePerTest(ctx.studente().getIndirizzoEmailIstituzionale());
        schermata.setTipologiaPerTest("INTERROGAZIONE");
        schermata.setDataPerTest(LocalDate.of(2026, 10, 3));
        schermata.setVotoPerTest(7.25f);
        schermata.setNotaPerTest("Buona prova");

        // Act: viene chiamato il VERO metodo dell'applicazione, void e senza parametri.
        schermata.registraValutazione();

        // Assert: verifichiamo solo la valutazione persistita, non i JOptionPane/notifiche.
        List<Valutazione> valutazioni = recuperaValutazioniStudente(
                ctx.studente().getIndirizzoEmailIstituzionale()
        );

        //Deve esserci esattamente una valutazione associata allo studente A
        assertEquals(1, valutazioni.size());

        //Con questi assert verifichiamo che sia esattamente la valutazione che abbiamo inserito come input
        Valutazione salvata = valutazioni.get(0);
        assertEquals(TipologiaValutazione.INTERROGAZIONE, salvata.getTipologia());
        assertEquals(7.25f, salvata.getVoto(), 0.0001f);
        //questo parametro delta indica la tolleranza accettata nello scarto tra i due valori, serve per evitare problemi
        //nella rappresentazione in virgola mobile

        assertEquals(LocalDate.of(2026, 10, 3), salvata.getData());
        assertEquals("Buona prova", salvata.getNotaDescrittiva());
    }

    // CASI DI ERRORE

    @Test
    @DisplayName("TC2: Tipologia non selezionata")
    void registraValutazione_TipologiaNonSelezionata_NonPersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc2", "CLS-RV0002");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        schermata.setStudentePerTest(ctx.studente().getIndirizzoEmailIstituzionale());
        schermata.setTipologiaPerTest(null);
        schermata.setDataPerTest(LocalDate.of(2026, 10, 3));
        schermata.setVotoPerTest(7.25f);
        schermata.setNotaPerTest("Buona prova");

        schermata.registraValutazione();

        //nessuna valutazione viene registrata
        assertEquals(
                0,
                numeroValutazioniStudente(
                        ctx.studente().getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC3: Voto non selezionato")
    void registraValutazione_VotoNonSelezionato_NonPersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc3", "CLS-RV0003");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        schermata.setStudentePerTest(ctx.studente().getIndirizzoEmailIstituzionale());
        schermata.setTipologiaPerTest("PROVA_SCRITTA");
        schermata.setDataPerTest(LocalDate.of(2026, 10, 3));
        schermata.setVotoPerTest(null);
        schermata.setNotaPerTest("Buona prova");

        schermata.registraValutazione();

        assertEquals(
                0,
                numeroValutazioniStudente(
                        ctx.studente().getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC4: Data successiva alla data corrente")
    void registraValutazione_DataFutura_NonPersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc4", "CLS-RV0004");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        schermata.setStudentePerTest(ctx.studente().getIndirizzoEmailIstituzionale());
        schermata.setTipologiaPerTest("INTERROGAZIONE");
        //Precondizione: data corrente precedente alla data di input
        schermata.setDataPerTest(LocalDate.of(2027, 12, 25));
        schermata.setVotoPerTest(6.5f);
        schermata.setNotaPerTest("");

        schermata.registraValutazione();

        assertEquals(
                0,
                numeroValutazioniStudente(
                        ctx.studente().getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC5: Data corrispondente a domenica")
    void registraValutazione_DataDomenica_NonPersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc5", "CLS-RV0005");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        schermata.setStudentePerTest(ctx.studente().getIndirizzoEmailIstituzionale());
        schermata.setTipologiaPerTest("PROVA_SCRITTA");
        //Precondizione: data corrente successiva alla data di input
        schermata.setDataPerTest(LocalDate.of(2026, 10, 4));
        schermata.setVotoPerTest(8.0f);
        schermata.setNotaPerTest("");

        schermata.registraValutazione();

        assertEquals(
                0,
                numeroValutazioniStudente(
                        ctx.studente().getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC6: Data non selezionata")
    void registraValutazione_DataNonSelezionata_NonPersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc6", "CLS-RV0006");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        schermata.setStudentePerTest(ctx.studente().getIndirizzoEmailIstituzionale());
        schermata.setTipologiaPerTest("INTERROGAZIONE");
        schermata.setDataPerTest(null);
        schermata.setVotoPerTest(5.75f);
        schermata.setNotaPerTest("Da migliorare");

        schermata.registraValutazione();

        assertEquals(
                0,
                numeroValutazioniStudente(
                        ctx.studente().getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC7: Nota troppo lunga (201 caratteri)")
    void registraValutazione_NotaTroppoLunga_NonPersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc7", "CLS-RV0007");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        schermata.setStudentePerTest(ctx.studente().getIndirizzoEmailIstituzionale());
        schermata.setTipologiaPerTest("PROVA_SCRITTA");
        schermata.setDataPerTest(LocalDate.of(2026, 10, 3));
        schermata.setVotoPerTest(9.0f);
        schermata.setNotaPerTest("N".repeat(201));

        schermata.registraValutazione();

        assertEquals(
                0,
                numeroValutazioniStudente(
                        ctx.studente().getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC8: Studente non selezionato")
    void registraValutazione_StudenteNonSelezionato_NonPersisteValutazione() throws Exception {
        TestContext ctx = preparaContesto("tc8", "CLS-RV0008");
        SchermataRegistraValutazione schermata = creaSchermata(ctx);

        schermata.setStudentePerTest(null);
        schermata.setTipologiaPerTest("INTERROGAZIONE");
        schermata.setDataPerTest(LocalDate.of(2026, 10, 3));
        schermata.setVotoPerTest(6.25f);
        schermata.setNotaPerTest("");

        schermata.registraValutazione();

        assertEquals(
                0,
                numeroValutazioniStudente(
                        ctx.studente().getIndirizzoEmailIstituzionale()
                )
        );
    }

    /** Raggruppa i dati prodotti dal setup del singolo test case. */
    private record TestContext(InfoClasseDTO classe, StudenteDTO studente) {
    }
}
