package it.unina.registroelettronico.Entity;


import it.unina.registroelettronico.DTO.StudenteDTO;
import it.unina.registroelettronico.Database.GestorePersistenza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SistemaRegistroElettronico {

    private static SistemaRegistroElettronico instance;
    private List<Utente> utenti;
    private List<ClasseVirtuale> classiVirtuali;
    private GestorePersistenza gestorePersistenza;


    private SistemaRegistroElettronico() {
        gestorePersistenza = new GestorePersistenza();
        utenti =
                gestorePersistenza.caricaTutti(
                        Utente.class
                );

        classiVirtuali =
                gestorePersistenza.caricaTutti(
                        ClasseVirtuale.class
                );

    }


    public static SistemaRegistroElettronico getInstance() {

        if (instance == null) {
            instance = new SistemaRegistroElettronico();
        }

        return instance;
    }

    public boolean verificaUnivocitaNomeClassePerDocente(String nomeClasse, String email) {

        Docente docente = cercaDocentePerEmail(email);

        return docente.verificaUnivocitaNome(nomeClasse);

    }

    private Docente cercaDocentePerEmail(String emailDocente) {

        for (Utente u : utenti) {
            if (u instanceof Docente) {
                if (u.getIndirizzoEmailIstituzionale().equals(emailDocente)) {
                    return (Docente) u;
                }
            }
        }

        //Non è possibile che il docente non venga trovato
        return null;
    }

    private String generaCodice() {
        String caratteri = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("CLS-");

        for (int i = 0; i < 6; i++) {
            sb.append(caratteri.charAt(random.nextInt(caratteri.length())));
        }

        return sb.toString();
    }

    private boolean verificaUnivocitaCodice(String codice) {
        for (ClasseVirtuale c : classiVirtuali) {
            if (c.getCodice().equals(codice))
                return false;
        }

        return true;
    }


    public String generaCodiceUnivoco() {

        String codice = "";

        do {
            codice = generaCodice();
        } while (!verificaUnivocitaCodice(codice));

        return codice;

    }


    public boolean cercaClasse(String codiceClasse, String nomeClasse) {

        for (ClasseVirtuale c : classiVirtuali) {
            if (c.getCodice().equals(codiceClasse)) {
                nomeClasse = c.getNome();
                return true;
            }
        }
        return false;

    }

    public boolean verificaIscrizione(String codiceClasse, String indirizzoEmailIstituzionale) {

        ClasseVirtuale classe = trovaClassePerCodice(codiceClasse);

        //non c'è il rischio di NullPointerException perché abbiamo verificato col metodo di prima che la classe esiste

        for (Studente s : classe.getStudenti()) {
            if (s.getIndirizzoEmailIstituzionale().equals(indirizzoEmailIstituzionale)) {
                return true;
            }
        }

        return false;


    }

    private ClasseVirtuale trovaClassePerCodice(String codice) {

        ClasseVirtuale classe = null;
        for (ClasseVirtuale c : classiVirtuali) {
            if (c.getCodice().equals(codice))
                classe = c;
        }

        return classe;

    }

    public List<List<String>> recuperaStudentiClasse(String codice) {

        ClasseVirtuale classe = trovaClassePerCodice(codice);

        List<List<String>> elenco_studenti=new ArrayList<>();

        //fare in modo che se non ci sono studenti per classe virtuale l'attributo non sia null, ma una lista vuota
        for (Studente s : classe.getStudenti()) {
            List<String> stud=new ArrayList<>();
            stud.add(s.getNome());
            stud.add(s.getCognome());
            stud.add(s.getIndirizzoEmailIstituzionale());

            elenco_studenti.add(stud);

        }

        return elenco_studenti;
    }

    public List<String> recuperaTipologieValutazione() {

        List<String> tipologie = new ArrayList<>();

        for (TipologiaValutazione tipologia :
                TipologiaValutazione.values()) {

            tipologie.add(tipologia.toString());
        }

        return tipologie;

    }

    public Registro recuperaRegistro(String codice) {

        ClasseVirtuale c=trovaClassePerCodice(codice);
        return c.getRegistro();

    }

    public boolean memorizzaClasse(String nomeClasse, String codice, String indirizzoEmail) {
        Docente docente =
                cercaDocentePerEmail(indirizzoEmail);

        if (docente == null) {//non accadrà mai
            return false;
        }

        ClasseVirtuale classe =
                new ClasseVirtuale(
                        nomeClasse,
                        codice,
                        docente
                );//qui nel costruttore è aggiunta anche tra le classi del docente

        boolean esitoPersistenza =
                gestorePersistenza.salva(classe);

        if (!esitoPersistenza) {
            return false;
        }

        classiVirtuali.add(classe);


        return true;

    }

    public boolean registraDocente(String indirizzoEmailIstituzionale, String nome, String cognome) {

        Docente docente=new Docente(indirizzoEmailIstituzionale,cognome,nome);

        utenti.add(docente);

        return gestorePersistenza.salva(docente);

    }

    public boolean registraStudente(String indirizzoEmailIstituzionale, String nome, String cognome) {
        Studente studente=new Studente(indirizzoEmailIstituzionale,cognome,nome);

        utenti.add(studente);

        return gestorePersistenza.salva(studente);

    }
}
