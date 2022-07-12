public class DettaglioSchedaLavori {
    public String tipologiaIntervento = "MECCANICA GENERALE";
    public String imporoIntervento;
    public String ivaIntervento;
    public String note;

    public String getTotaleIntervento() {
        float importo = Float.valueOf(this.imporoIntervento);
        float iva = Float.valueOf(this.ivaIntervento);
        return String.format("%.2f", importo + ((importo * iva) / 100));
    }

    @Override
    public String toString() {
        return "\n\ttipologiaIntervento='" + tipologiaIntervento + '\'' +
                ", \n\tnote='" + note + '\'' +
                ", \n\timporoIntervento='" + getTotaleIntervento() + '\'';
    }
}
