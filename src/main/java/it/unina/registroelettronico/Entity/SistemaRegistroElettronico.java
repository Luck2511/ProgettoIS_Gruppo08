package it.unina.registroelettronico.Entity;


import it.unina.registroelettronico.DTO.StudenteDTO;
import it.unina.registroelettronico.Database.GestorePersistenza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SistemaRegistroElettronico {

    private static SistemaRegistroElettronico instance=null;
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


    public boolean cercaClasse(String codiceClasse, StringBuilder nomeClasse) {

        for (ClasseVirtuale c : classiVirtuali) {
            if (c.getCodice().equals(codiceClasse)) {
                nomeClasse.replace(0, nomeClasse.length(), c.getNome());
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

        return gestorePersistenza.salvaUtenteSeNonPresente(docente,indirizzoEmailIstituzionale);

    }

    public boolean registraStudente(String indirizzoEmailIstituzionale, String nome, String cognome) {
        Studente studente=new Studente(indirizzoEmailIstituzionale,cognome,nome);

        utenti.add(studente);

        return gestorePersistenza.salvaUtenteSeNonPresente(studente,indirizzoEmailIstituzionale);

    }

    public List<List<String>> recuperaClassiDocente(String emailDocente) {

        Docente docente =
                cercaDocentePerEmail(emailDocente);

        List<List<String>> elencoClassi =
                new ArrayList<>();

        for (ClasseVirtuale classe :
                docente.getClassiGestite()) {

            List<String> infoClasse =
                    new ArrayList<>();

            infoClasse.add(classe.getNome());
            infoClasse.add(classe.getCodice());

            elencoClassi.add(infoClasse);
        }

        return elencoClassi;
    }

    public List<List<String>> recuperaClassiStudente(
            String emailStudente
    ) {

        Studente studente =
                cercaStudentePerEmail(emailStudente);

        List<List<String>> elencoClassi =
                new ArrayList<>();

        for (ClasseVirtuale classe :
                studente.getClassiVirtuali()) {

            List<String> infoClasse =
                    new ArrayList<>();

            infoClasse.add(classe.getNome());
            infoClasse.add(classe.getCodice());

            elencoClassi.add(infoClasse);
        }

        return elencoClassi;
    }

    private Studente cercaStudentePerEmail(String emailStudente) {
        for (Utente u : utenti) {
            if (u instanceof Studente) {
                if (u.getIndirizzoEmailIstituzionale().equals(emailStudente)) {
                    return (Studente) u;
                }
            }
        }

        //Non è possibile che lo studente non venga trovato
        return null;

    }

    public boolean iscriviStudenteAllaClasse(String codiceClasse,String emailStudente) {

        ClasseVirtuale classe =
                trovaClassePerCodice(codiceClasse);

        Studente studente =
                cercaStudentePerEmail(emailStudente);

        if (classe == null || studente == null) {
            return false;
        }//in realtà lo studente deve esistere perché autenticato e la classe deve esistere perché il controllo
        //in ControllerClassi è stato superato, quindi qui non dovrebbe mai ritornare false


        // Modifichiamo il lato proprietario della ManyToMany
        classe.aggiungiStudente(studente);

        try {

            /*
             * ClasseVirtuale possiede la @JoinTable,
             * quindi aggiornando la classe Hibernate
             * aggiorna anche classe_studente.
             */
            gestorePersistenza.aggiorna(classe);

        } catch (RuntimeException e) {

            // Ripristiniamo anche lo stato in memoria
            classe.rimuoviStudente(studente);

            /*
            * In Creazione Classe Virtuale non serviva il try catch per rollbackare gli aggiornamenti
            * dell'Entity perché in realtà facevamo prima la persistenza in DB e poi la memorizzazione nell'entity
            * e se la persistenza andava male restituivamo subito false. Invece in questo caso d'uso siamo costretti
            * prima a lavorare nell'entity e poi nel DB perché ne ha bisogno Hibernate per gestire gli aggiornamenti
            * in maniera automatica perché deve vedere che la relazione è cambiata. In più se guardiamo i metodi di
            * GestorePersistenza, salva() che usiamo per la Creazione Classe Virtuale fa un catch di RunTimeException
            * interno che restituisce false nell'handler, invece aggiorna() che usiamo qui rilancia l'eccezione che quindi
            * va ricatturata
            * */

            return false;
        }

        //Aggiorniamo anche il lato inverso in memoria lato Entity.

        studente.aggiungiClasse(classe);

        return true;
    }





}
