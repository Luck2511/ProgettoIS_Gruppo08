package it.unina.registroelettronico.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Valutazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipologiaValutazione tipologia;

    @Column(nullable = false)
    private float voto;

    @Column(length = 1000)
    private String notaDescrittiva;


    @ManyToOne(optional = false,
            fetch = FetchType.EAGER)
    @JoinColumn(
            name = "studente_id",
            nullable = false
    )
    private Studente studente;


    public Valutazione(LocalDate data, TipologiaValutazione tipologia, float voto, String notaDescrittiva, Studente studente) {
        this.data = data;
        this.tipologia = tipologia;
        this.voto = voto;
        this.notaDescrittiva = notaDescrittiva;
        this.studente = studente;
    }

    public Valutazione(){

    }

    public Studente getStudente() {
        return studente;
    }

    public TipologiaValutazione getTipologia() {
        return tipologia;
    }

    public LocalDate getData() {
        return data;
    }

    public float getVoto() {
        return voto;
    }

    public String getNotaDescrittiva() {
        return notaDescrittiva;
    }
}
