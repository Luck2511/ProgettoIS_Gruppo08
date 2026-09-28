package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Studente extends Utente{

    @ManyToMany(mappedBy = "studenti")
    private List<ClasseVirtuale> classiVirtuali;

    public Studente(String indirizzoEmailIstituzionale, String cognome, String nome, List<ClasseVirtuale> classiVirtuali) {
        super(indirizzoEmailIstituzionale, cognome, nome);

        //classivirtuali da valutare
    }

    public Studente(){

    }



}
