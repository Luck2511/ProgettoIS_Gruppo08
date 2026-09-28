package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Lezione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;


    @Column(nullable = false)
    private String argomento;

    @Column(length = 2000)
    private String descrizione;


    public Lezione(){

    }


}
