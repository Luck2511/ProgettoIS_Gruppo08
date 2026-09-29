package it.unina.registroelettronico.Controller;


import it.unina.registroelettronico.Controller.Exceptions.ClasseEsistenteException;
import it.unina.registroelettronico.Controller.Exceptions.ClasseInesistenteException;
import it.unina.registroelettronico.Controller.Exceptions.GiaIscrittoException;
import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.InfoClasseDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;
import it.unina.registroelettronico.Entity.SistemaRegistroElettronico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControllerClassi {

    private static ControllerClassi instance=null;
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

        classeCreata.setNome(nomeClasse);
        classeCreata.setCodice(codice);

        //memorizzazione classe (e registro) e gestione persistenza
        boolean esitoMemorizzazione =
                sre.memorizzaClasse(
                        nomeClasse,
                        codice,
                        indirizzoEmail
                );

        if (!esitoMemorizzazione) {
            return false;
        }

        return true;

    }

    public boolean iscrizioneStudenteTramiteCodiceClasse(String codiceClasse, InfoClasseDTO classe, StudenteDTO studente) throws ClasseInesistenteException,GiaIscrittoException{

        //ricerca classe associata al codice

        StringBuilder nomeClasse=new StringBuilder("");

        boolean esitoRicerca=sre.cercaClasse(codiceClasse,nomeClasse);//nomeClasse parametro d'uscita
        if(!esitoRicerca){
            throw new ClasseInesistenteException("Classe inesistente");
        }

        //verificare iscrizione studente

        boolean isSubscribed=sre.verificaIscrizione(codiceClasse,studente.getIndirizzoEmailIstituzionale());

        //se non è iscritto
        if(!isSubscribed){

            boolean esitoIscrizione =
                    sre.iscriviStudenteAllaClasse(
                            codiceClasse,
                            studente.getIndirizzoEmailIstituzionale()
                    );

            if (!esitoIscrizione) {
                return false;
            }


            classe.setNome(nomeClasse.toString());
            classe.setCodice(codiceClasse);

            return true;
        }
        else{
            //se già iscritto
            throw new GiaIscrittoException("Studente già iscritto");
        }



    }

    public List<StudenteDTO> recuperaStudenti(String codice) {

        List<List<String>> elenco_studenti=sre.recuperaStudentiClasse(codice);

        List<StudenteDTO> studenti=new ArrayList<>();


        for(List<String> stud:elenco_studenti){

            studenti.add(new StudenteDTO(stud.get(0),stud.get(1),stud.get(2)));

        }


        return studenti;

    }

    public List<InfoClasseDTO> recuperaClassiDocente(String indirizzoEmailIstituzionale) {

        List<List<String>> elencoClassi =
                sre.recuperaClassiDocente(indirizzoEmailIstituzionale);

        List<InfoClasseDTO> classi =
                new ArrayList<>();

        for (List<String> classe : elencoClassi) {

            classi.add(
                    new InfoClasseDTO(
                            classe.get(0),
                            classe.get(1)
                    )
            );
        }

        return classi;
    }

    public List<InfoClasseDTO> recuperaClassiStudente(String indirizzoEmailIstituzionale) {

        List<List<String>> elencoClassi =
                sre.recuperaClassiStudente(indirizzoEmailIstituzionale);

        List<InfoClasseDTO> classi =
                new ArrayList<>();

        for (List<String> classe : elencoClassi) {

            classi.add(
                    new InfoClasseDTO(
                            classe.get(0),
                            classe.get(1)
                    )
            );
        }

        return classi;

    }
}
