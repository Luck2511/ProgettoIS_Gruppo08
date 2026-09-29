package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Studente extends Utente{

    @ManyToMany(mappedBy = "studenti",
            fetch = FetchType.EAGER)
    private List<ClasseVirtuale> classiVirtuali=new ArrayList<>();

    public Studente(String indirizzoEmailIstituzionale, String cognome, String nome) {
        super(indirizzoEmailIstituzionale, cognome, nome);


    }

    public Studente(){

    }



}
