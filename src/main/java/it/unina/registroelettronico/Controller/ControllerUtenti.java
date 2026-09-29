package it.unina.registroelettronico.Controller;

import it.unina.registroelettronico.DTO.DocenteDTO;
import it.unina.registroelettronico.DTO.StudenteDTO;
import it.unina.registroelettronico.Entity.SistemaRegistroElettronico;

public class ControllerUtenti {

    private static ControllerUtenti instance=null;
    private SistemaRegistroElettronico sre;

    private ControllerUtenti(){
        sre=SistemaRegistroElettronico.getInstance();
    }

    public static ControllerUtenti getInstance(){
        if(instance==null){
            instance=new ControllerUtenti();
        }

        return instance;

    }

    public boolean registraDocente(DocenteDTO docente) {

        return sre.registraDocente(docente.getIndirizzoEmailIstituzionale(),docente.getNome(),docente.getCognome());


    }

    public boolean registraStudente(StudenteDTO studente) {
        return sre.registraStudente(studente.getIndirizzoEmailIstituzionale(),studente.getNome(),studente.getCognome());
    }
}
