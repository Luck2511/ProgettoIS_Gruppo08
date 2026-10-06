package it.unina.registroelettronico.Boundary.Swing;

import it.unina.registroelettronico.Controller.ControllerClassi;
import it.unina.registroelettronico.Controller.ControllerUtenti;
import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;
import it.unina.registroelettronico.Database.JpaUtil;
import it.unina.registroelettronico.Entity.SistemaRegistroElettronico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JTextField;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite di Test - Iscrizione Tramite Codice Classe")
class IscrizioneCodiceClasseTest {

    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = JpaUtil.getInstance().getEntityManager();
        svuotaTabelle();
    }

    /**
     * Svuota il database reale prima di ciascun test case.
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

    /** Crea e persiste lo studente autenticato richiesto dalla precondizione. */
    //Serve per la precondizione sullo studente autenticato
    private StudenteDTO creaStudenteAutenticato(String suffisso) {
        StudenteDTO studente = new StudenteDTO(
                "Studente",
                "Test",
                "studente." + suffisso + "@test.unina.it",
                "password-test"
        );

        //assertTrue() perché se la registrazione dello studente fallisce, la precondizione:
        //Studente autenticato/esistente
        //non è stata predisposta.
        //Quindi non avrebbe senso proseguire.

        assertTrue(ControllerUtenti.getInstance().registraStudente(studente));
        return studente;
    }

    /**
     * Crea la classe con il codice esatto previsto dal test case.
     *
     * Per questa precondizione viene usato SistemaRegistroElettronico perché il normale
     * ControllerClassi genera automaticamente il codice e non permette di imporne uno.
     */
    private void creaClasseConCodice(String codice, String nome, String suffissoDocente) {
        DocenteDTO docente = new DocenteDTO(
                "Docente",
                "Test",
                "docente." + suffissoDocente + "@test.unina.it",
                "password-test"
        );

        assertTrue(ControllerUtenti.getInstance().registraDocente(docente));
        assertTrue(
                SistemaRegistroElettronico.getInstance().memorizzaClasse(
                        nome,
                        codice,
                        docente.getIndirizzoEmailIstituzionale()
                )
        );

        //questi assertTrue servono sempre per non procedere se non si riescono a costruire le precondizioni
    }

    /** Conta nel DB l'associazione fra lo studente e la classe indicata. */
    //La domanda che fa è:
    //quante associazioni esistono tra questo studente e questa specifica classe?
    private long numeroIscrizioni(String codiceClasse, String emailStudente) {
        em.clear();

        return em.createQuery(
                        "SELECT COUNT(c) FROM ClasseVirtuale c JOIN c.studenti s " +
                                "WHERE c.codice = :codice " +
                                "AND s.indirizzoEmailIstituzionale = :email",
                        Long.class
                )
                .setParameter("codice", codiceClasse)
                .setParameter("email", emailStudente)
                .getSingleResult();
    }

    /** Conta tutte le iscrizioni persistite per lo studente. */
    //questo metodo invece chiede semplicemente:
    //quante iscrizioni ha questo studente complessivamente?
    private long numeroTotaleIscrizioniStudente(String emailStudente) {
        em.clear();

        return em.createQuery(
                        "SELECT COUNT(c) FROM ClasseVirtuale c JOIN c.studenti s " +
                                "WHERE s.indirizzoEmailIstituzionale = :email",
                        Long.class
                )
                .setParameter("email", emailStudente)
                .getSingleResult();
    }

    //CASO DI SUCCESSO

    @Test
    @DisplayName("TC1: Codice valido")
    void iscrizioneTramiteCodice_CodiceValido_IscriveStudente() {
        // Arrange: classe esistente e studente non ancora iscritto.
        //Precondizione: studente autenticato
        StudenteDTO studente = creaStudenteAutenticato("tc1");
        //Precondizione: esiste una classe con codice "CLS-A23BC6" a cui lo studente non è iscritto
        //la non iscrizione dello studente è certa in virtù del fatto che svuotiamo le tabelle col beforeEach
        creaClasseConCodice("CLS-A23BC6", "Classe TC1", "tc1");

        SchermataStudente schermataStudente = new SchermataStudente(studente);

        // Componenti fittizi richiesti dalla firma del vero metodo della Boundary.
        JTextField txtCodiceClasse = new JTextField("CLS-A23BC6");
        JLabel lblErrore = new JLabel();

        // Act: viene chiamato esattamente il metodo utilizzato dall'applicazione.
        schermataStudente.confermaIscrizioneClasse(
                null,
                txtCodiceClasse,
                lblErrore
        );

        // Assert: verifichiamo soltanto l'effetto persistito, non la GUI.
        //verifichaimo che il numero di iscrizioni dello studente alla classe è esattamente 1
        assertEquals(
                1,
                numeroIscrizioni(
                        "CLS-A23BC6",
                        studente.getIndirizzoEmailIstituzionale()
                )
        );
    }

    // CASI DI ERRORE

    @Test
    @DisplayName("TC2: Codice inesistente")
    void iscrizioneTramiteCodice_CodiceInesistente_NonIscriveStudente() {
        //Precondizione: studente autenticato
        //Precondizione: nessuna classe possiede il codice "CLS-999999",
        //ne siamo certi perché abbiamo svuotato le tabelle
        StudenteDTO studente = creaStudenteAutenticato("tc2");
        SchermataStudente schermataStudente = new SchermataStudente(studente);

        JTextField txtCodiceClasse = new JTextField("CLS-999999");
        JLabel lblErrore = new JLabel();

        // Il metodo reale intercetta ClasseInesistenteException e termina il flusso.
        schermataStudente.confermaIscrizioneClasse(
                null,
                txtCodiceClasse,
                lblErrore
        );


        assertEquals(
                0,
                numeroTotaleIscrizioniStudente(
                        studente.getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC3: Studente già iscritto")
    void iscrizioneTramiteCodice_StudenteGiaIscritto_NonDuplicaIscrizione() throws Exception {
        //Precondizione: studente autenticato
        StudenteDTO studente = creaStudenteAutenticato("tc3");
        //Precondizione: esiste la classe con codice "CLS-123456"
        creaClasseConCodice("CLS-123456", "Classe TC3", "tc3");

        // Precondizione: lo studente risulta già iscritto alla classe.
        InfoClasseDTO classe = new InfoClasseDTO("", "");
        assertTrue(//qua lo iscrive la prima volta
                ControllerClassi.getInstance().iscrizioneStudenteTramiteCodiceClasse(
                        "CLS-123456",
                        classe,
                        studente
                )
        );

        SchermataStudente schermataStudente = new SchermataStudente(studente);
        JTextField txtCodiceClasse = new JTextField("CLS-123456");
        JLabel lblErrore = new JLabel();

        // Act: il vero metodo intercetta GiaIscrittoException e non duplica la relazione.
        schermataStudente.confermaIscrizioneClasse(
                null,
                txtCodiceClasse,
                lblErrore
        );

        //nessuna nuova iscrizione, quindi deve mantenere una sola iscrizione
        assertEquals(
                1,
                numeroIscrizioni(
                        "CLS-123456",
                        studente.getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC4: Codice con lunghezza errata")
    void iscrizioneTramiteCodice_LunghezzaErrata_NonIscriveStudente() {
        //Precondizione: studente autenticato
        StudenteDTO studente = creaStudenteAutenticato("tc4");
        SchermataStudente schermataStudente = new SchermataStudente(studente);

        JTextField txtCodiceClasse = new JTextField("CLS-12345");//codice con lunghezza errata
        JLabel lblErrore = new JLabel();

        schermataStudente.confermaIscrizioneClasse(
                null,
                txtCodiceClasse,
                lblErrore
        );


        assertEquals(
                0,
                numeroTotaleIscrizioniStudente(
                        studente.getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC5: Prefisso del codice errato")
    void iscrizioneTramiteCodice_PrefissoErrato_NonIscriveStudente() {
        //Precondizione: studente autenticato
        StudenteDTO studente = creaStudenteAutenticato("tc5");
        SchermataStudente schermataStudente = new SchermataStudente(studente);

        JTextField txtCodiceClasse = new JTextField("ABC-123456");//prefisso del codice errato
        JLabel lblErrore = new JLabel();

        schermataStudente.confermaIscrizioneClasse(
                null,
                txtCodiceClasse,
                lblErrore
        );

        assertEquals(
                0,
                numeroTotaleIscrizioniStudente(
                        studente.getIndirizzoEmailIstituzionale()
                )
        );
    }

    @Test
    @DisplayName("TC6: Codice non inserito")
    void iscrizioneTramiteCodice_CodiceVuoto_NonIscriveStudente() {
        //Precondizione: studente autenticato
        StudenteDTO studente = creaStudenteAutenticato("tc6");
        SchermataStudente schermataStudente = new SchermataStudente(studente);

        JTextField txtCodiceClasse = new JTextField("");//codice non inserito
        JLabel lblErrore = new JLabel();

        schermataStudente.confermaIscrizioneClasse(
                null,
                txtCodiceClasse,
                lblErrore
        );

        assertEquals(
                0,
                numeroTotaleIscrizioniStudente(
                        studente.getIndirizzoEmailIstituzionale()
                )
        );
    }
}
