package it.unina.registroelettronico.DTO;

public class StudenteDTO {

    private String nome;
    private String cognome;
    private String indirizzoEmailIstituzionale;
    private String password;

    public StudenteDTO(String nome,String cognome,String indirizzoEmailIstituzionale,String password){
        this.nome=nome;
        this.cognome=cognome;
        this.indirizzoEmailIstituzionale=indirizzoEmailIstituzionale;
        this.password=password;
    }

    public StudenteDTO(String nome,String cognome,String indirizzoEmailIstituzionale){
        this.nome=nome;
        this.cognome=cognome;
        this.indirizzoEmailIstituzionale=indirizzoEmailIstituzionale;
        this.password="";
    }

    public String getIndirizzoEmailIstituzionale() {
        return indirizzoEmailIstituzionale;
    }

    public String getPassword(){return password;}

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }
}
