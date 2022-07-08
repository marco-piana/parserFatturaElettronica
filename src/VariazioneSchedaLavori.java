import java.util.ArrayList;

public class VariazioneSchedaLavori {
    public String targa;
    public String officina;
    public String dataInizioLavori;
    public String oraInizioLavori = "08:00";
    public String dataFineLavori;
    public String oraFineLavori = "09:00";
    public String lavoroAlKm;
    public String capitoloImpegnoCodice = "1982";
    public String capitoloImpegnoAnno;
    public String note;
    public ArrayList<DettaglioSchedaLavori> dettagli = new ArrayList<>();

    public String nomeFile;
    public String tipoDocumento;
    public String dataDocumento;
    public String numeroDocumento;
    public String totaleDocumento;
    public String numeroOrdine;
    public String totaleOrdine;

    @Override
    public String toString() {
        return "VariazioneSchedaLavori{" +
                "targa='" + targa + '\'' +
                ", officina='" + officina + '\'' +
                ", dataInizioLavori='" + dataInizioLavori + '\'' +
                ", oraInizioLavori='" + oraInizioLavori + '\'' +
                ", dataFineLavori='" + dataFineLavori + '\'' +
                ", oraFineLavori='" + oraFineLavori + '\'' +
                ", lavoroAlKm='" + lavoroAlKm + '\'' +
                ", capitoloImpegnoCodice='" + capitoloImpegnoCodice + '\'' +
                ", capitoloImpegnoAnno='" + capitoloImpegnoAnno + '\'' +
                ", note='" + note + '\'' +
                ", dettagli=" + dettagli +
                ", nomeFile='" + nomeFile + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", dataDocumento='" + dataDocumento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", totaleDocumento='" + totaleDocumento + '\'' +
                ", numeroOrdine='" + numeroOrdine + '\'' +
                ", totaleOrdine='" + totaleOrdine + '\'' +
                '}';
    }
}
