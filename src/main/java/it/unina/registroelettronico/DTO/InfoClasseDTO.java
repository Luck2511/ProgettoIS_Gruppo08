package it.unina.registroelettronico.DTO;

public class InfoClasseDTO {

    private String nome;
    private String codice;

    public InfoClasseDTO(String nome, String codice) {
        this.nome = nome;
        this.codice = codice;
    }

    public String getCodice() {
        return codice;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }
}
