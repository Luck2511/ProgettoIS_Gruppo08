package it.unina.registroelettronico.DTO;

public class StudenteDTO {

    private String nome;
    private String cognome;
    private String indirizzoEmailIstituzionale;

    public StudenteDTO(String nome,String cognome,String indirizzoEmailIstituzionale){
        this.nome=nome;
        this.cognome=cognome;
        this.indirizzoEmailIstituzionale=indirizzoEmailIstituzionale;

    }

    public String getIndirizzoEmailIstituzionale() {
        return indirizzoEmailIstituzionale;
    }


    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }
}
