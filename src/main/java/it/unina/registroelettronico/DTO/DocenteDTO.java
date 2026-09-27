package it.unina.registroelettronico.DTO;

public class DocenteDTO {

    private String nome;
    private String cognome;
    private String indirizzoEmailIstituzionale;


    public DocenteDTO(String nome,String cognome,String indirizzoEmailIstituzionale){
        this.nome=nome;
        this.cognome=cognome;
        this.indirizzoEmailIstituzionale=indirizzoEmailIstituzionale;

    }

    public String getIndirizzoEmailIstituzionale() {
        return indirizzoEmailIstituzionale;
    }



}
