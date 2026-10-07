package it.unina.registroelettronico.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Suite di Test Strutturale - Cerca Valutazioni Studente")
class CercaValutazioniStudenteTest {

    private Registro registro;
    private Studente studenteA;
    private Studente studenteB;

    @BeforeEach
    void setUp() {//questo metodo eseguito prima di ogni test case contiene le precondizioni comuni

        registro = new Registro();

        studenteA = new Studente(
                "studente.a@test.unina.it",
                "Rossi",
                "Mario",
                "password-test"
        );

        studenteB = new Studente(
                "studente.b@test.unina.it",
                "Bianchi",
                "Luigi",
                "password-test"
        );
    }

    private Valutazione creaValutazione(Studente studente) {

        return new Valutazione(
                LocalDate.of(2026, 10, 3),
                TipologiaValutazione.INTERROGAZIONE,
                7.25f,
                "Buona prova",
                studente
        );
    }


    // TC1 - Il registro non contiene valutazioni
    // Cammino: START - 1 - 2(F) - 10 - END

    @Test
    @DisplayName("TC1: Registro senza valutazioni")
    void cercaValutazioniStudente_ListaVuota_RestituisceListaVuota() {

        // Arrange
        //Precondizione: non ci sono valutazioni registrate
        registro.setValutazioniRegistratePerTest(List.of());

        // Act
        List<List<String>> risultato =
                registro.cercaValutazioniStudente(
                        studenteA.getIndirizzoEmailIstituzionale()
                );

        // Assert
        assertTrue(
                risultato.isEmpty(),
                "Il risultato deve essere vuoto se il registro non contiene valutazioni"
        );
    }



    // TC2 - È presente una valutazione dello studente cercato
    // Cammino: START - 1 - 2(T) - 3(T) - 4 - 5 - 6 - 7 - 8 - 9
    //          - 2(F) - 10 - END


    @Test
    @DisplayName("TC2: Valutazione appartenente allo studente cercato")
    void cercaValutazioniStudente_EmailCorrispondente_RestituisceValutazione() {

        // Arrange
        //Precondizione: è presente una valutazione associata a studente A
        Valutazione valutazioneStudenteA =
                creaValutazione(studenteA);

        registro.setValutazioniRegistratePerTest(
                List.of(valutazioneStudenteA)
        );

        // Act
        List<List<String>> risultato =
                registro.cercaValutazioniStudente(
                        studenteA.getIndirizzoEmailIstituzionale()
                );

        // Assert
        //deve esserci una valutazione
        assertEquals(
                1,
                risultato.size(),
                "Deve essere restituita una sola valutazione"
        );

        //deve essere esattamente quella inserita
        assertEquals(
                List.of(
                        "2026-10-03",
                        "INTERROGAZIONE",
                        "7.25",
                        "Buona prova"
                ),
                risultato.get(0),
                "I dati della valutazione restituita devono coincidere con quelli registrati"
        );
    }

    // TC3 - È presente una valutazione di uno studente diverso
    // Cammino: START - 1 - 2(T) - 3(F) - 2(F) - 10 - END


    @Test
    @DisplayName("TC3: Valutazione appartenente a uno studente diverso")
    void cercaValutazioniStudente_EmailDiversa_RestituisceListaVuota() {

        // Arrange
        //Precondizione: è presente una valutazione associata allo studente B
        Valutazione valutazioneStudenteB =
                creaValutazione(studenteB);

        registro.setValutazioniRegistratePerTest(
                List.of(valutazioneStudenteB)
        );

        // Act
        List<List<String>> risultato =
                registro.cercaValutazioniStudente(
                        studenteA.getIndirizzoEmailIstituzionale()
                );

        // Assert
        assertTrue(
                risultato.isEmpty(),
                "Non devono essere restituite valutazioni appartenenti a un altro studente"
        );
    }

}
