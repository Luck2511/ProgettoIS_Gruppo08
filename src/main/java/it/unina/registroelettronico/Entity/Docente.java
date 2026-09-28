package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Docente extends Utente{

    @OneToMany(mappedBy = "docente")
    private List<ClasseVirtuale> classiGestite;


    public Docente(String indirizzoEmailIstituzionale, String cognome, String nome) {
        super(indirizzoEmailIstituzionale,cognome,nome);
        //VEDI per le classi
    }

    public Docente(){

    }

    public boolean verificaUnivocitaNome(String nomeClasse) {

        for(ClasseVirtuale c:classiGestite){
            if(c.getNome().equals(nomeClasse))
                return false;
        }

        return true;

    }


}
