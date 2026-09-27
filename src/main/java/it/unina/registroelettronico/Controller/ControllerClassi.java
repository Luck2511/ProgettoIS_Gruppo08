package it.unina.registroelettronico.Controller;


import it.unina.registroelettronico.Controller.Exceptions.ClasseEsistenteException;
import it.unina.registroelettronico.Controller.Exceptions.ClasseInesistenteException;
import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;
import it.unina.registroelettronico.Entity.SistemaRegistroElettronico;

public class ControllerClassi {

    private static ControllerClassi instance;
    private SistemaRegistroElettronico sre;

    private ControllerClassi(){
        sre=SistemaRegistroElettronico.getInstance();
    }

    public static ControllerClassi getInstance(){
        if(instance==null){
            instance=new ControllerClassi();
        }

        return instance;

    }


    public boolean creaClasse(String nomeClasse, InfoClasseDTO classeCreata, DocenteDTO docente) throws ClasseEsistenteException{

        //verifica univocità nome classe

        String indirizzoEmail=docente.getIndirizzoEmailIstituzionale();

        boolean esitoVerifica=sre.verificaUnivocitaNomeClassePerDocente(nomeClasse,indirizzoEmail);

        if(!esitoVerifica){
            throw new ClasseEsistenteException("Nome classe già usato");
        }

        //generazione codice univoco

        String codice;
        codice=sre.generaCodiceUnivoco();

        classeCreata=new InfoClasseDTO(nomeClasse,codice);

        //memorizzazione classe (e registro) e gestione persistenza
        //TODO FARE QUESTA PARTE

        //in caso di errori nella memorizzazione o nella persistenza fare qui un return false

        //conferma creazione classe

        return true;

    }

    public boolean iscrizioneStudenteTramiteCodiceClasse(String codiceClasse, InfoClasseDTO classe, StudenteDTO studente) throws ClasseInesistenteException{

        //ricerca classe associata al codice

        String nomeClasse="";

        boolean esitoRicerca=sre.cercaClasse(codiceClasse,nomeClasse);//nomeClasse parametro d'uscita
        if(!esitoRicerca){
            throw new ClasseInesistenteException("Classe inesistente");
        }

        //verificare iscrizione studente

        boolean isSubscribed=sre.verificaIscrizione(codiceClasse,studente.getIndirizzoEmailIstituzionale());

        //se non è iscritto
        if(!isSubscribed){
            //se lo studente non è iscritto aggiungiamo lo studente alla classe (memorizzazione e persistenza)
            //TODO FARE QUESTA PARTE

            classe=new InfoClasseDTO(nomeClasse,codiceClasse);
            return true;
        }

        //se già iscritto
        return false;


    }
}
