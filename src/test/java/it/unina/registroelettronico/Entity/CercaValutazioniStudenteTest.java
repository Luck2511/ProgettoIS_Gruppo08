package it.unina.registroelettronico.Entity;

import it.unina.registroelettronico.Database.GestorePersistenza;
import it.unina.registroelettronico.Database.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test strutturali del metodo Registro.cercaValutazioniStudente(String).
 *
 * Il metodo ha complessità ciclomatica V(G) = 3; vengono quindi
 * considerati i tre cammini linearmente indipendenti individuati nel CFG.
 *
 * Le valutazioni necessarie come precondizione vengono create tramite
 * il vero metodo Registro.registraValutazione(...), come avviene nel progetto.
 */
@DisplayName("Test strutturale - Registro.cercaValutazioniStudente")
class CercaValutazioniStudenteTest {

    private EntityManager em;
    private Registro registro;
    private Studente studenteA;
    private Studente studenteB;

    /*
     * Prima di ogni test:
     * 1) apriamo un EntityManager collegato al database reale;
     * 2) puliamo le tabelle coinvolte;
     * 3) creiamo Docente, Studente A, Studente B e una ClasseVirtuale;
     * 4) iscriviamo entrambi gli studenti alla classe;
     * 5) rendiamo persistente la precondizione comune e recuperiamo il Registro della classe.
     *
     * In questo modo ogni test parte da un Registro reale ma senza valutazioni.
     */
    @BeforeEach
    void setUp() {
        em = JpaUtil.getInstance().getEntityManager();
        svuotaTabelle();

        Docente docente = new Docente(
                "docente.strutturale@test.unina.it",
                "Rossi",
                "Mario",
                "password-test"
        );

        studenteA = new Studente(
                "studente.a@test.unina.it",
                "Bianchi",
                "Anna",
                "password-test"
        );

        studenteB = new Studente(
                "studente.b@test.unina.it",
                "Verdi",
                "Luca",
                "password-test"
        );

        ClasseVirtuale classe = new ClasseVirtuale(
                "Classe test strutturale",
                "CLS-STR001",
                docente
        );

        // La relazione viene predisposta sui due lati, come nel dominio reale.
        classe.aggiungiStudente(studenteA);
        studenteA.collegaClasse(classe);

        classe.aggiungiStudente(studenteB);
        studenteB.collegaClasse(classe);

        GestorePersistenza gestorePersistenza = new GestorePersistenza();

        //assertTrue perché se qualcosa va male nella costruzione della precondizione è inutile continuare
        assertTrue(
                gestorePersistenza.salvaTutti(
                        docente,
                        studenteA,
                        studenteB,
                        classe
                ),
                "La precondizione del test deve essere persistita correttamente"
        );

        registro = classe.getRegistro();

        //assertNotNull perché senza registro non possiamo testare il metodo e quindi è inutile continuare
        assertNotNull(
                registro,
                "La ClasseVirtuale deve possedere il proprio Registro"
        );
    }

    /*
     * Svuota il database prima di ogni test.
     *
     * Le tabelle vengono eliminate in un ordine che rispetta le relazioni
     * e le foreign key presenti nel modello JPA.
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


    // CAMMINO 1
    // START-1-2(F)-10-END

    /*
     * Il Registro non contiene valutazioni.
     * Il ciclo for non entra mai nel corpo e il metodo restituisce
     * direttamente una lista vuota.
     */
    @Test
    @DisplayName("TC1: Registro senza valutazioni")
    void cercaValutazioniStudente_ListaVuota_RestituisceListaVuota() {

        // Act: nessuna valutazione è stata registrata nell'Arrange.
        List<List<String>> risultato =
                registro.cercaValutazioniStudente(
                        studenteA.getIndirizzoEmailIstituzionale()
                );

        // Assert.
        assertTrue(risultato.isEmpty()); //testiamo che la lista restituita sia vuota
    }


    // CAMMINO 2
    // START-1-2(T)-3(T)-4-5-6-7-8-9-2(F)-10-END


    /**
     * Nel Registro esiste una valutazione appartenente proprio allo
     * Studente A. Il ramo vero dell'if viene percorso e il metodo costruisce
     * la lista di stringhe contenente data, tipologia, voto e nota.
     */
    @Test
    @DisplayName("TC2: Valutazione appartenente allo studente cercato")
    void cercaValutazioniStudente_EmailCorrispondente_RestituisceValutazione() {

        // Arrange specifico: la valutazione è registrata per lo studente A
        boolean registrata = registro.registraValutazione(
                studenteA.getIndirizzoEmailIstituzionale(),
                "INTERROGAZIONE",
                7.25f,
                LocalDate.of(2026, 10, 3),
                "Buona prova"
        );

        //Non ha senso procedere se non riusciamo a impostare la precondizione
        assertTrue(
                registrata,
                "La valutazione dello Studente A deve essere registrata per predisporre il test"
        );

        // Act.
        List<List<String>> risultato =
                registro.cercaValutazioniStudente(
                        studenteA.getIndirizzoEmailIstituzionale()
                );

        // Assert: deve essere restituita esattamente la valutazione creata.

        //c'è solo la valutazione inserita da noi nella precondizione
        assertEquals(1, risultato.size());

        //deve essere esattamente la valutazione che abbiamo inserito
        assertEquals(
                List.of(
                        "2026-10-03",
                        "INTERROGAZIONE",
                        "7.25",
                        "Buona prova"
                ),
                risultato.get(0)
        );
    }


    // CAMMINO 3
    // START-1-2(T)-3(F)-2(F)-10-END

    /*
     * Nel Registro esiste una valutazione, ma appartiene allo Studente B.
     * Cercando lo Studente A, il ciclo viene eseguito una volta ma la
     * condizione dell'if risulta falsa; la lista restituita resta vuota.
     */
    @Test
    @DisplayName("TC3: Valutazione appartenente a un altro studente")
    void cercaValutazioniStudente_EmailDiversa_RestituisceListaVuota() {

        // Arrange specifico: la valutazione viene registrata per lo studente B
        boolean registrata = registro.registraValutazione(
                studenteB.getIndirizzoEmailIstituzionale(),
                "INTERROGAZIONE",
                7.25f,
                LocalDate.of(2026, 10, 3),
                "Buona prova"
        );

        //Non ha senso procedere se non riusciamo a impostare la precondizione
        assertTrue(
                registrata,
                "La valutazione dello Studente B deve essere registrata per predisporre il test"
        );

        // Act: viene cercato lo Studente A.
        List<List<String>> risultato =
                registro.cercaValutazioniStudente(
                        studenteA.getIndirizzoEmailIstituzionale()
                );

        // Assert: la valutazione di B non deve comparire tra quelle di A. Quindi risultato deve essere vuoto.

        assertTrue(risultato.isEmpty());
    }



}
