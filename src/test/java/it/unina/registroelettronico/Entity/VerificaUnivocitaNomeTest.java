package it.unina.registroelettronico.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Test strutturali del metodo Docente.verificaUnivocitaNome(String).
 *
 * Il metodo ha complessità ciclomatica V(G) = 3, quindi vengono
 * progettati tre test case, uno per ciascun cammino linearmente
 * indipendente individuato nel Control Flow Graph.

 */
@DisplayName("Test strutturale - Docente.verificaUnivocitaNome")
class VerificaUnivocitaNomeTest {

    private Docente docente;

    /**
     * Crea un docente nuovo, che per costruzione non gestisce ancora
     * alcuna classe virtuale. Viene usato come stato iniziale comune
     * dei test.
     */
    private Docente creaDocente() {
        return new Docente(
                "docente@test.unina.it",
                "Rossi",
                "Mario",
                "password-test"
        );
    }

    //setup che crea prima di ogni test il docente
    @BeforeEach
    void setUp() {

        docente = new Docente(
                "docente@test.unina.it",
                "Rossi",
                "Mario",
                "password"
        );
    }



    /**
     * TC1 - Cammino: START-1(F)-4-END
     *
     * Il docente non gestisce alcuna classe, quindi il ciclo for
     * non viene eseguito e il metodo deve restituire true.
     */
    @Test
    @DisplayName("TC1: Nessuna classe gestita - nome disponibile")
    void verificaUnivocitaNome_ListaVuota_RestituisceTrue() {

        // Arrange: docente senza classi gestite.

        // Act: verifica di un nome qualsiasi.
        boolean risultato = docente.verificaUnivocitaNome("3A Informatica");

        // Assert: non esistendo classi, il nome deve risultare disponibile.
        assertTrue(risultato);
    }

    /**
     * TC2 - Cammino: START-1(T)-2(T)-3-END
     *
     * Il docente gestisce una classe con lo stesso nome cercato.
     * Il ciclo viene eseguito e la condizione dell'if risulta vera,
     * quindi il metodo deve terminare immediatamente con false.
     */
    @Test
    @DisplayName("TC2: Classe con lo stesso nome - nome non disponibile")
    void verificaUnivocitaNome_NomeGiaPresente_RestituisceFalse() {

        // Arrange: il costruttore di ClasseVirtuale collega automaticamente
        // la classe al docente mediante docente.aggiungiClasse(this).

        new ClasseVirtuale(
                "3A Informatica",
                "CLS-AAA111",
                docente
        );

        // Act.
        boolean risultato = docente.verificaUnivocitaNome("3A Informatica");

        // Assert: il nome è già utilizzato dallo stesso docente.
        assertFalse(risultato);
    }

    /**
     * TC3 - Cammino: START-1(T)-2(F)-1(F)-4-END
     *
     * Il docente gestisce una sola classe, ma il suo nome è diverso
     * da quello cercato. L'if risulta falso, il ciclo termina e il
     * metodo deve restituire true.
     */
    @Test
    @DisplayName("TC3: Classe con nome diverso - nome disponibile")
    void verificaUnivocitaNome_NomeDiverso_RestituisceTrue() {

        // Arrange: esiste una classe, ma con un nome differente.

        new ClasseVirtuale(
                "4B Matematica",
                "CLS-BBB222",
                docente
        );

        // Act.
        boolean risultato = docente.verificaUnivocitaNome("3A Informatica");

        // Assert: nessuna classe gestita usa il nome cercato.
        assertTrue(risultato);
    }
}
