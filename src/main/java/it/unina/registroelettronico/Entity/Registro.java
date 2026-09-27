package it.unina.registroelettronico.Entity;

import it.unina.registroelettronico.Database.GestorePersistenza;

import java.util.List;

public class Registro {

    private ClasseVirtuale classeVirtuale;
    private List<Compito> compitiRegistrati;
    private List<Valutazione> valutazioniRegistrate;
    private List<Lezione> lezioniRegistrate;

    private GestorePersistenza gestorePersistenza;


    public Registro(){
        //da valutare
    }
}
