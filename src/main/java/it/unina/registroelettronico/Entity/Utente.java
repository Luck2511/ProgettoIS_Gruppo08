package it.unina.registroelettronico.Entity;

public class Utente {

    private String nome;
    private String cognome;
    private String indirizzoEmailIstituzionale;

    public Utente(String indirizzoEmailIstituzionale, String cognome, String nome) {
        this.indirizzoEmailIstituzionale = indirizzoEmailIstituzionale;
        this.cognome = cognome;
        this.nome = nome;
    }



    public String getIndirizzoEmailIstituzionale() {
        return indirizzoEmailIstituzionale;
    }
}
