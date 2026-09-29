package it.unina.registroelettronico.Entity;

import it.unina.registroelettronico.Database.GestorePersistenza;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "registro",
            fetch = FetchType.EAGER)
    private ClasseVirtuale classeVirtuale;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "registro_id")
    private List<Compito> compitiRegistrati=new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "registro_id")
    private List<Valutazione> valutazioniRegistrate=new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "registro_id")
    private List<Lezione> lezioniRegistrate=new ArrayList<>();

    @Transient //serve per non renderlo persistente e farlo ignorare da Hibernate
    private GestorePersistenza gestorePersistenza;

    public Registro(ClasseVirtuale classeVirtuale){
        this.classeVirtuale=classeVirtuale;
    }

    public Registro(){

    }

    public List<List<String>> cercaValutazioniStudente(String emailStudente) {

        List<List<String>> valStudente=new ArrayList<>();

        for(Valutazione v:valutazioniRegistrate){

            if(v.getStudente().getIndirizzoEmailIstituzionale().equals(emailStudente)){
                List<String> val=new ArrayList<>();

                val.add(v.getData().toString());
                val.add(v.getTipologia().toString());
                val.add(Float.toString(v.getVoto()));
                val.add(v.getNotaDescrittiva());

                valStudente.add(val);

            }

        }

        return valStudente;

    }
}
