package it.unina.registroelettronico.DTO;

public class DocenteDTO {

    private String nome;
    private String cognome;
    private String indirizzoEmailIstituzionale;
    private String password;

    public DocenteDTO(String nome,String cognome,String indirizzoEmailIstituzionale, String password){
        this.nome=nome;
        this.cognome=cognome;
        this.indirizzoEmailIstituzionale=indirizzoEmailIstituzionale;
        this.password=password;
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

    public String getPassword() {return password;}
}
