package it.unina.registroelettronico.Entity;

import java.util.List;

public class Docente extends Utente{

    private List<ClasseVirtuale> classiGestite;


    public Docente(String indirizzoEmailIstituzionale, String cognome, String nome) {
        super(indirizzoEmailIstituzionale,cognome,nome);
        //VEDI per le classi
    }



    public boolean verificaUnivocitaNome(String nomeClasse) {

        for(ClasseVirtuale c:classiGestite){
            if(c.getNome().equals(nomeClasse))
                return false;
        }

        return true;

    }
}
