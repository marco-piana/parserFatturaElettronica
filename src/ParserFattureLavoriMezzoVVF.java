import org.codehaus.stax2.XMLInputFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserFattureLavoriMezzoVVF
    {

        private static final Logger logger = LoggerFactory.getLogger(ParserFattureLavoriMezzoVVF.class);

        private String fileXML;
        private String tipoDocumento;
        private String dataDocumento;
        private String numeroDocumento;
        private String totaleDocumento;
        private String numeroOrdine;
        private String dataOrdine;
        private String descrizioneOrdine;
        private String parzialePrezzoOrdine;
        private String ivaParzialePrezzoOrdine;
        private String cedente;
        private String targaCorrente;
        private ArrayList<VariazioneSchedaLavori> variazioniCorrenti = new ArrayList<>();
        private VariazioneSchedaLavori variazioneCorrente = null;
        private float price;

        public boolean isFirstVariazione = true;

        public static void main(String[] args) {
            boolean MULTIFILES = true;
            String dirFattureXML = "Z:\\Autorimessa-fatture\\2022\\Fortini\\";

            // Istanza del parser
            ParserFattureLavoriMezzoVVF parserVVF = new ParserFattureLavoriMezzoVVF();

            // Elenco dei file nella dirFattureXML
            File[] files = new File(dirFattureXML).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });

//            logger.info("--------------------------------------");
            if(MULTIFILES) {
                for (int i = 0; i < files.length; i++) {
                    System.out.println("File " + files[i].getName());
                    parserVVF.resetParser();
                    parserVVF.fileXML = files[i].getName();
                    try {
                        parserVVF.extractWorkFromBill(dirFattureXML + files[i].getName());
                        if (parserVVF.isFirstVariazione && (parserVVF.variazioneCorrente != null)) {
                            parserVVF.variazioniCorrenti.add(parserVVF.variazioneCorrente);
                        }
                        if (
                                parserVVF.variazioneCorrente.tipoDocumento.equals("TD01") ||
                                parserVVF.variazioneCorrente.tipoDocumento.equals("TD24")

                        ) {
                            System.out.println(parserVVF.variazioneCorrente.toString());
                        }
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    logger.info("--------------------------------------");
                }
//                for(VariazioneSchedaLavori v : parserVVF.variazioniCorrenti) {
//                    if (v.tipoDocumento.equals("TD01")) {
//                        System.out.println(v.toString());
//                    }
//                }
            }
            else {
                String FILENAME = dirFattureXML + "Carr_Elio_22_12_01_2022.xml";
//                String FILENAME = "C:\\Users\\davide.piana\\IdeaProjects\\parserFatturaElettronica\\src\\test.xml";
                System.out.println("File " + FILENAME);
                parserVVF.fileXML = FILENAME;
                try {
                    parserVVF.extractWorkFromBill(FILENAME);
                    System.out.println(parserVVF.variazioneCorrente.toString());
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.info("--------------------------------------");
            }

            //TODO: scorrere ogni file xml delle fatture nella cartella

        }

        private void resetParser() {
            this.variazioneCorrente = null;
            this.targaCorrente = null;
        }

        /**
         * Estrae i dati necessari all'inserimento nella Variazione Lavori Mezzo del GAC e ne genera gli oggetti
         * corrispondenti
         * @param file path assoluta del file XML della fattura di lavoro del mezzo
         * @throws FactoryConfigurationError
         * @throws XMLStreamException
         * @throws IOException
         */
        public void extractWorkFromBill(String file)
                throws FactoryConfigurationError, XMLStreamException, IOException
        {
            long startTime = System.nanoTime();
            // set up a Woodstox reader

            XMLInputFactory xmlif = XMLInputFactory2.newInstance();

            XMLStreamReader xmlStreamReader = xmlif.createXMLStreamReader(new FileInputStream(file), "UTF-8");
            boolean isValidForPrice = false;
            String TAG_PRINCIPALE = "";
            try
            {
                while (xmlStreamReader.hasNext())
                {
                    xmlStreamReader.next();

                    // If 4 event, meaning just some random '\n' or something, we skip.
                    if (xmlStreamReader.isCharacters())
                    {
                        continue;
                    }

                    // Il tag principale deve avere all'interno dei tag con nome univoco
                    if (xmlStreamReader.isStartElement())
                    {
                        switch (xmlStreamReader.getLocalName()) {
                            case "CedentePrestatore":
                                TAG_PRINCIPALE = xmlStreamReader.getLocalName();
                                break;
                            case "DettaglioLinee":
                                TAG_PRINCIPALE = xmlStreamReader.getLocalName();
                                break;
                            case "DatiOrdineAcquisto":
                                TAG_PRINCIPALE = xmlStreamReader.getLocalName();
                                break;
                            case "DatiGeneraliDocumento":
                                TAG_PRINCIPALE = xmlStreamReader.getLocalName();
                                break;


                            case "Denominazione":
                                if(TAG_PRINCIPALE.equals("CedentePrestatore")) {
                                    this.cedente = xmlStreamReader.getElementText();
                                    logger.debug(TAG_PRINCIPALE + ": " + this.cedente);
                                }
                                break;


                            case "Descrizione":
                                if(TAG_PRINCIPALE.equals("DettaglioLinee")) {
                                    String dettaglio = xmlStreamReader.getElementText();
                                    String testoTag = "";
                                    if (dettaglio.contains("/D ") ||
                                       (dettaglio.contains("/M ")) ||
                                       (dettaglio.contains("/DS ")) ||
                                       (dettaglio.startsWith("S/R "))
                                    ) {
                                        testoTag = dettaglio.substring(3);
                                    }
                                    else {
                                        testoTag = dettaglio;
                                    }

                                    // Ricavo la targa del mezzo se presente nella descrizione
                                    if (testoTag.contains("( VF")) {
                                        int position = testoTag.indexOf("( VF ");
                                        String fromTarga = testoTag.substring(position+2, testoTag.length()-1);
                                        testoTag = testoTag.replaceAll("\\( VF .+\\)", "");
                                        String targa = fromTarga.split("\\)")[0].replace(" ", "");
                                        this.targaCorrente = targa;
                                        logger.debug(TAG_PRINCIPALE + ": " + this.targaCorrente);
                                        this.createNewVariazione(targa);

                                    }
                                    else if (testoTag.contains("TARGA: VF ")) {
                                        int position = testoTag.indexOf("TARGA: VF ");
                                        String fromTarga = testoTag.substring(position, "TARGA: VF ".length()+5);
                                        testoTag = testoTag.substring("TARGA: VF ".length()+5, testoTag.length());
                                        String targa = fromTarga.substring(fromTarga.length()-5);

                                        this.targaCorrente = targa;
                                        logger.debug(TAG_PRINCIPALE + ": " + this.targaCorrente);
                                        this.createNewVariazione(targa);

                                    }
                                    else if (testoTag.contains("VF")) {
                                        int position = testoTag.indexOf("VF");
                                        String fromTarga = testoTag.substring(position, testoTag.length()-1);
                                        String targa = fromTarga.split(" ")[0];
                                        this.targaCorrente = targa;
                                        logger.debug(TAG_PRINCIPALE + ": " + this.targaCorrente);
                                        this.createNewVariazione(targa);
                                        this.descrizioneOrdine = testoTag;
                                    }

                                    // Esclude alcune righe in base al loro contenuto
                                    if(!(testoTag.contains("con Iva non incassata") || testoTag.contains("----"))) {
                                        isValidForPrice = true;
                                        this.descrizioneOrdine = testoTag;
                                        logger.debug(TAG_PRINCIPALE + ": " + this.descrizioneOrdine);
                                    }
                                }
                                break;
                            case "PrezzoTotale":
                                if(TAG_PRINCIPALE.equals("DettaglioLinee")) {
                                    String testoTag = xmlStreamReader.getElementText();

                                    // Esclude alcune righe in base al loro contenuto
                                    if(isValidForPrice) {
                                        System.out.println(testoTag + "\n");
                                        this.parzialePrezzoOrdine = testoTag;
                                        logger.debug(TAG_PRINCIPALE + ": " + this.parzialePrezzoOrdine);
                                    }
                                }
                                break;
                             case "RiferimentoTesto":
                                if(TAG_PRINCIPALE.equals("DettaglioLinee")) {
                                    String testoTag = xmlStreamReader.getElementText();

                                    // Esclude alcune righe in base al loro contenuto
                                    if(!testoTag.toLowerCase().contains("VOSTRO ORDINE".toLowerCase())) {
//                                        System.out.println("------------------" + testoTag);
                                        this.variazioneCorrente.dettagli.get(this.variazioneCorrente.dettagli.size()-1).note += " " + testoTag;
                                        logger.debug(TAG_PRINCIPALE + ": " + this.descrizioneOrdine);
                                    }
                                }
                                break;
                            case "AliquotaIVA":
                                if(TAG_PRINCIPALE.equals("DettaglioLinee")) {
                                    String testoTag = xmlStreamReader.getElementText();
                                    // Esclude alcune righe in base al loro contenuto
                                    if(isValidForPrice) {
                                        this.ivaParzialePrezzoOrdine = testoTag;
                                        logger.debug(TAG_PRINCIPALE + ": " + this.ivaParzialePrezzoOrdine);
                                        this.price += Float.valueOf(this.parzialePrezzoOrdine) + (Float.valueOf(this.parzialePrezzoOrdine) * Float.valueOf(this.ivaParzialePrezzoOrdine) / 100);
                                        this.addDettaglioScheda();
                                    }
                                }
                                break;


                            case "IdDocumento":
                                if(TAG_PRINCIPALE.equals("DatiOrdineAcquisto")) {
                                    this.numeroOrdine =  xmlStreamReader.getElementText();
                                    logger.debug(TAG_PRINCIPALE + ": " + this.numeroOrdine);
                                }
                                break;
                            case "Data": // Oppure NumItem
                                if(TAG_PRINCIPALE.equals("DatiOrdineAcquisto")) {
                                    this.dataOrdine = xmlStreamReader.getElementText();
                                    LocalDate d = LocalDate.parse(this.dataOrdine);
                                    this.dataOrdine = d.format(DateTimeFormatter.ofPattern("dd/MM/YYYY")).toString();
                                    logger.debug(TAG_PRINCIPALE + ": " + this.dataOrdine);
                                }
                                else if (TAG_PRINCIPALE.equals("DatiGeneraliDocumento")) {
                                    this.dataDocumento = xmlStreamReader.getElementText();
                                    LocalDate d = LocalDate.parse(this.dataDocumento);
                                    this.dataDocumento = d.format(DateTimeFormatter.ofPattern("dd/MM/YYYY")).toString();
                                    logger.debug(TAG_PRINCIPALE + ": " + this.dataDocumento);
                                }
                                break;
                            case "NumItem": // Oppure Data
                                if(TAG_PRINCIPALE.equals("DatiOrdineAcquisto")) {
                                    Pattern pattern = Pattern.compile("(..)\\/(..)\\/(....)");
                                    Matcher matcher = pattern.matcher(xmlStreamReader.getElementText());
                                    if (matcher.find())
                                    {
                                        this.dataOrdine = matcher.group();
                                        LocalDate d = LocalDate.parse(this.dataOrdine);
                                        this.dataOrdine = d.format(DateTimeFormatter.ofPattern("dd/MM/YYYY")).toString();
                                        logger.debug(TAG_PRINCIPALE + ": " + this.dataOrdine);
                                    }
                                }
                                break;


                            case "ImportoTotaleDocumento":
                                if(TAG_PRINCIPALE.equals("DatiGeneraliDocumento")) {
                                    this.totaleDocumento = xmlStreamReader.getElementText();
                                    logger.debug(TAG_PRINCIPALE + ": " + this.totaleDocumento);
                                }
                                break;
                            case "Numero":
                                if(TAG_PRINCIPALE.equals("DatiGeneraliDocumento")) {
                                    this.numeroDocumento = xmlStreamReader.getElementText();
                                    logger.debug(TAG_PRINCIPALE + ": " + this.numeroDocumento);
                                }
                                break;
                            case "TipoDocumento":
                                if(TAG_PRINCIPALE.equals("DatiGeneraliDocumento")) {
                                    this.tipoDocumento = xmlStreamReader.getElementText();
                                    logger.debug(TAG_PRINCIPALE + ": " + this.tipoDocumento);
                                }
                                break;
                            default:
                                break;
                        }
                        continue;
                    }

                    // If we are at an end element that is the rowTag, so at the end of the record, we want to do a couple of things
                    if (xmlStreamReader.isEndElement() && xmlStreamReader.getLocalName().equalsIgnoreCase(TAG_PRINCIPALE))
                    {
                        switch (xmlStreamReader.getLocalName()) {
                            case "DettaglioLinee":
                                TAG_PRINCIPALE = "";
                                isValidForPrice = false;
                                break;
                            default:
                                TAG_PRINCIPALE = "";
                                break;
                        }
                    }

                }
            }
            catch (Exception e)
            {
                logger.error("Error! " + e.toString());
            }
            finally
            {
                xmlStreamReader.close();
            }
            long endTime   = System.nanoTime();
            long totalTime = endTime - startTime;
//            logger.info("Done! Time took: {}", totalTime / 1000000000);
        }

        private void addDettaglioScheda() {
            DettaglioSchedaLavori dettaglio = new DettaglioSchedaLavori();
            dettaglio.imporoIntervento = this.parzialePrezzoOrdine;
            dettaglio.note = this.descrizioneOrdine;
            dettaglio.ivaIntervento = this.ivaParzialePrezzoOrdine;
            variazioneCorrente.totaleOrdine = this.price;
            variazioneCorrente.dettagli.add(dettaglio);
        }

        private void createNewVariazione(String targa) {
            if (variazioneCorrente != null) {
                this.isFirstVariazione = false;
                variazioniCorrenti.add(variazioneCorrente);
            }
            this.parzialePrezzoOrdine = String.valueOf(0);
            this.descrizioneOrdine = "";
            this.price = 0;
            variazioneCorrente = new VariazioneSchedaLavori();
            variazioneCorrente.targa = targa;
            variazioneCorrente.nomeFile = fileXML;
            variazioneCorrente.tipoDocumento = this.tipoDocumento;
            variazioneCorrente.dataDocumento = this.dataDocumento;
            variazioneCorrente.numeroDocumento = this.numeroDocumento;
            variazioneCorrente.totaleDocumento = this.totaleDocumento;
            variazioneCorrente.numeroOrdine = this.numeroOrdine;
            variazioneCorrente.officina = this.cedente;
            variazioneCorrente.dataOrdine = this.dataOrdine != null ? this.dataOrdine : this.dataDocumento;
            variazioneCorrente.note = this.descrizioneOrdine;
        }

    }

