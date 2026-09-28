package it.unina.registroelettronico.DTO;



import java.time.LocalDate;

public class InfoValutazioneDTO {

    private LocalDate data;
    private String tipologia;//nel DTO lo mettiamo come stringa, per non avere contatti con l'Entity
    private float voto;
    private String notaDescrittiva;


    public InfoValutazioneDTO(LocalDate data, String tipologia, float voto, String notaDescrittiva) {
        this.data = data;
        this.tipologia = tipologia;
        this.voto = voto;
        this.notaDescrittiva = notaDescrittiva;
    }

    public LocalDate getData() {
        return data;
    }

    public String getTipologia() {
        return tipologia;
    }

    public float getVoto() {
        return voto;
    }

    public String getNotaDescrittiva() {
        return notaDescrittiva;
    }


}
