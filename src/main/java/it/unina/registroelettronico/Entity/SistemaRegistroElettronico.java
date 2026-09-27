package it.unina.registroelettronico.Entity;


import it.unina.registroelettronico.Database.GestorePersistenza;

import java.util.List;
import java.util.Random;

public class SistemaRegistroElettronico {

    private static SistemaRegistroElettronico instance;
    private List<Utente> utenti;
    private List<ClasseVirtuale> classiVirtuali;
    private GestorePersistenza gestorePersistenza;


    public SistemaRegistroElettronico() {
        gestorePersistenza = new GestorePersistenza();
       // utenti = gestorePersistenza.caricaUtenti();
     //   classiVirtuali = gestorePersistenza.caricaClassiVirtuali();

    }



    public static SistemaRegistroElettronico getInstance(){

        if(instance==null){
            instance=new SistemaRegistroElettronico();
        }

        return instance;
    }

    public boolean verificaUnivocitaNomeClassePerDocente(String nomeClasse,String email){

        Docente docente = cercaDocentePerEmail(email);

        return docente.verificaUnivocitaNome(nomeClasse);

    }

    private Docente cercaDocentePerEmail(String emailDocente){

        for(Utente u:utenti){
            if(u instanceof Docente){
                if(u.getIndirizzoEmailIstituzionale().equals(emailDocente)){
                    return (Docente)u;
                }
            }
        }

        //Non è possibile che il docente non venga trovato
        return null;
    }

    private String generaCodice(){
        String caratteri = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("CLS-");

        for (int i = 0; i < 6; i++) {
            sb.append(caratteri.charAt(random.nextInt(caratteri.length())));
        }

        return sb.toString();
    }

    private boolean verificaUnivocitaCodice(String codice){
        for(ClasseVirtuale c:classiVirtuali){
            if(c.getCodice().equals(codice))
                return false;
        }

        return true;
    }


    public String generaCodiceUnivoco(){

        String codice="";

        do{
            codice=generaCodice();
        }while(!verificaUnivocitaCodice(codice));

        return codice;

    }


    public boolean cercaClasse(String codiceClasse, String nomeClasse) {

        for(ClasseVirtuale c:classiVirtuali){
            if(c.getCodice().equals(codiceClasse)){
                nomeClasse=c.getNome();
                return true;
            }
        }
        return false;

    }

    public boolean verificaIscrizione(String codiceClasse, String indirizzoEmailIstituzionale) {

        ClasseVirtuale classe=null;
        for(ClasseVirtuale c:classiVirtuali){
            if(c.getCodice().equals(codiceClasse))
                classe=c;

        }

        //non c'è il rischio di NullPointerException perché abbiamo verificato col metodo di prima che la classe esiste

        for(Studente s:classe.getStudenti()){
            if(s.getIndirizzoEmailIstituzionale().equals(indirizzoEmailIstituzionale)){
                return true;
            }
        }

        return false;


    }
}
