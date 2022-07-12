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
    public float totaleOrdine;
    public String dataOrdine;

    @Override
    public String toString() {

        return "VariazioneSchedaLavori {\n" +
                "\ttarga='" + targa.substring(2) + '\'' +
                ", \n\ttipoDocumento='" + tipoDocumento + '\'' +
                ", \n\tofficina='" + officina + '\'' +
                ", \n\tdataDocumento='" + dataDocumento + '\'' +
                ", \n\tdataOrdine='" + dataOrdine + '\'' +
                ", \n\tdataInizioLavori='" + dataInizioLavori + '\'' +
                ", \n\toraInizioLavori='" + oraInizioLavori + '\'' +
                ", \n\tnote='" + note + '\'' +
                "\n\tdettagli:" + dettagli +
                "\n" +
                "\ttotaleOrdine='" + String.format("%.2f", totaleOrdine) + '\'' +
                "\n" +
                "\n\tdataFineLavori='" + dataFineLavori + '\'' +
                ", \n\toraFineLavori='" + oraFineLavori + '\'' +
                ", \n\tlavoroAlKm='" + lavoroAlKm + '\'' +
//                ", \ncapitoloImpegnoCodice='" + capitoloImpegnoCodice + '\'' +
//                ", \ncapitoloImpegnoAnno='" + capitoloImpegnoAnno + '\'' +
//                ", \nnomeFile='" + nomeFile + '\'' +
//                ", \nnumeroDocumento='" + numeroDocumento + '\'' +
//                ", \ntotaleDocumento='" + totaleDocumento + '\'' +
//                ", \nnumeroOrdine='" + numeroOrdine + '\'' +
                "\n}";
    }
}
