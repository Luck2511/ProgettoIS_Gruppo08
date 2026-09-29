package it.unina.registroelettronico.Controller;


import it.unina.registroelettronico.DTO.InfoValutazioneDTO;
import it.unina.registroelettronico.Entity.Registro;
import it.unina.registroelettronico.Entity.SistemaRegistroElettronico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControllerRegistro {

    private static ControllerRegistro instance=null;
    private SistemaRegistroElettronico sre;


    private ControllerRegistro(){
        sre=SistemaRegistroElettronico.getInstance();
    }

    public static ControllerRegistro getInstance(){
        if(instance==null){
            instance=new ControllerRegistro();
        }

        return instance;

    }


    public List<String> recuperaTipologieValutazione() {
        return sre.recuperaTipologieValutazione();
    }

    public boolean registraValutazione(String codice, String emailStudente, String tipologia, float voto, LocalDate data, String nota) {

        //TODO MEMORIZZAZIONE PERSISTENZA E INVIO NOTIFICA
        return true;
    }

    public List<InfoValutazioneDTO> recuperaValutazioniStudente(String codice, String emailStudente) {
        Registro registro=sre.recuperaRegistro(codice);


        List<List<String>> valutazioni=registro.cercaValutazioniStudente(emailStudente);

        List<InfoValutazioneDTO> valutazioniDTO=new ArrayList<>();

        for(List<String> valutazione:valutazioni){
            float voto=Float.parseFloat(valutazione.get(2));
            LocalDate data=LocalDate.parse(valutazione.get(0));
            valutazioniDTO.add(new InfoValutazioneDTO(data,valutazione.get(1),voto,valutazione.get(3)));
        }

        return valutazioniDTO;

    }
}
