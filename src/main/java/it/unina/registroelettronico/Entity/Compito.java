package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Compito {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    @Column(length = 2000)
    private String descrizione;

    @Column(nullable = false)
    private LocalDate dataAssegnazione;

    @Column(nullable = false)
    private LocalDate dataScadenza;

    public Compito(String titolo, String descrizione, LocalDate dataAssegnazione, LocalDate dataScadenza) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataAssegnazione = dataAssegnazione;
        this.dataScadenza=dataScadenza;
    }

    public Compito(){

    }

}
