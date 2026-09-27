package it.unina.registroelettronico.Entity;

import java.util.List;

public class Studente extends Utente{

    private List<ClasseVirtuale> classiVirtuali;

    public Studente(String indirizzoEmailIstituzionale, String cognome, String nome, List<ClasseVirtuale> classiVirtuali) {
        super(indirizzoEmailIstituzionale, cognome, nome);

        //classivirtuali da valutare
    }



}
