package it.unina.registroelettronico.Entity;

import it.unina.registroelettronico.Comunicazione.GestoreNotifiche;
import it.unina.registroelettronico.Database.GestorePersistenza;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private GestorePersistenza gestorePersistenza=new GestorePersistenza();

    @Transient
    private GestoreNotifiche gestoreNotifiche=new GestoreNotifiche();

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

    private Valutazione aggiungiValutazione(
            LocalDate data,
            TipologiaValutazione tipologia,
            float voto,
            String nota,
            Studente studente
    ) {


        Valutazione valutazione =
                new Valutazione(
                        data,
                        tipologia,
                        voto,
                        nota,
                        studente
                );

        valutazioniRegistrate.add(valutazione);

        return valutazione;
    }

    private void rimuoviValutazione(
            Valutazione valutazione
    ) {
        valutazioniRegistrate.remove(valutazione);
    }

    public boolean registraValutazione(
            String emailStudente,
            String tipologia,
            float voto,
            LocalDate data,
            String nota
    ) {

        // ricerca dello studente nella classe associata al registro
        Studente studente = null;

        for (Studente s : classeVirtuale.getStudenti()) {

            if (s.getIndirizzoEmailIstituzionale()
                    .equals(emailStudente)) {

                studente = s;
                break;
            }
        }

        if (studente == null) {//scenario che non accadrà mai
            return false;
        }

        // conversione della stringa nella tipologia enum
        TipologiaValutazione tipoValutazione;

        try {
            tipoValutazione =
                    TipologiaValutazione.valueOf(tipologia);
        } catch (IllegalArgumentException e) {
            return false;
        }

        /*
        // creazione della valutazione
        Valutazione valutazione =
                new Valutazione(
                        data,
                        tipoValutazione,
                        voto,
                        nota,
                        studente
                );

        //Ora stesso discorso del caso d'uso Iscrizione tramite codice classe


        // modifica dello stato dell'Entity
        valutazioniRegistrate.add(valutazione);
        */


        Valutazione valutazione=aggiungiValutazione(data,tipoValutazione,voto,nota,studente);


        // persistenza
        try {

            Registro registroAggiornato =
                    gestorePersistenza.aggiorna(this);

            /*
             Va risincronizzato in questo punto con le valutazioni registrate nel database perché
             quando prima del try-catch facciamo valutazioniRegistrate.add(valutazione) non assegniamo l'id e
             quindi hibernate vede l'id null e non sa che quell'oggetto Valutazione è già stato memorizzato
             in maniere persistente, perciò in successive registrazioni nel fare il merge() andrebbe a rimemorizzare nel database
             valutazioni già memorizzate al suo interno
             */
            this.valutazioniRegistrate =
                    new ArrayList<>(
                            registroAggiornato.valutazioniRegistrate
                    );

        } catch (RuntimeException e) {

            // rollback dello stato in memoria
            /*
            valutazioniRegistrate.remove(valutazione);
            */
            rimuoviValutazione(valutazione);

            return false;
        }

        inviaNotifica("Hai una nuova valutazione da visualizzare nel registro della classe "+classeVirtuale.getNome()+"!",emailStudente);

        return true;

    }

    private void inviaNotifica(String descrizione,String emailStudente){

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String dataEOra= LocalDateTime.now().format(formato);

        List<String> notifica=new ArrayList<>();
        notifica.add(descrizione);
        notifica.add(emailStudente);
        notifica.add(dataEOra);

        gestoreNotifiche.inviaNotifica(notifica);

    }






}
