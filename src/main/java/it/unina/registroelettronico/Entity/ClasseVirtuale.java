package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ClasseVirtuale {

    @Column(nullable = false)
    private String nome;

    @Id
    private String codice;

    @ManyToOne(optional = false,
                fetch = FetchType.EAGER)
    @JoinColumn(name = "docente_id",
            nullable = false)
    private Docente docente;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "classe_studente",
            joinColumns =
                @JoinColumn(name = "classe_codice"),
            inverseJoinColumns =
                @JoinColumn(name = "studente_id")
    )
    private List<Studente> studenti=new ArrayList<>();

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "registro_id",
            unique = true
    )
    private Registro registro;


    public ClasseVirtuale(String nome,String codice,Docente docente){

        this.nome=nome;
        this.codice=codice;
        this.docente=docente;

        // Composizione ClasseVirtuale - Registro:
        // il Registro nasce insieme alla ClasseVirtuale.
        this.registro = new Registro(this);

        this.docente.aggiungiClasse(this);



    }

    public ClasseVirtuale(){

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

    public Registro getRegistro() {
        return registro;
    }
}
