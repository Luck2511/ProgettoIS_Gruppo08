package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(
            nullable = false,
            unique = true
    )
    private String indirizzoEmailIstituzionale;

    @Column(nullable = false)
    private String password;

    public Utente(String indirizzoEmailIstituzionale, String cognome, String nome) {
        this.indirizzoEmailIstituzionale = indirizzoEmailIstituzionale;
        this.cognome = cognome;
        this.nome = nome;
    }



    public Utente(){

    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getIndirizzoEmailIstituzionale() {
        return indirizzoEmailIstituzionale;
    }
}
