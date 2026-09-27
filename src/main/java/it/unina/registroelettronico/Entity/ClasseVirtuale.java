package it.unina.registroelettronico.Entity;

import java.util.List;

public class ClasseVirtuale {

    private String nome;
    private String codice;
    private Docente docente;
    private List<Studente> studenti;
    private Registro registro;


    public ClasseVirtuale(String nome,String codice,Docente docente){

        this.nome=nome;
        this.codice=codice;
        this.docente=docente;

        //studenti poi ci pensi

        //pure registro poi ci pensi

    }

    public String getNome(){
        return nome;
    }

    public String getCodice() {
        return codice;
    }


    public List<Studente> getStudenti() {
        return studenti;
    }
}
