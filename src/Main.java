import org.codehaus.stax2.XMLInputFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileReader;
import java.io.IOException;

    public class Main
    {

        private static final Logger logger = LoggerFactory.getLogger(Main.class);

        public static void main(String[] args) {
            Main p = new Main();
            //TODO: scorrere ogni file xml delle fatture nella cartella
            try {
                p.extractWorkFromBill("C:\\Users\\davide.piana\\IdeaProjects\\parserFatturaElettronica\\src\\test.xml");
            } catch (XMLStreamException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private float price = 0;

       public void extractWorkFromBill(String file)
                throws FactoryConfigurationError, XMLStreamException, IOException
        {
            long startTime = System.nanoTime();
            // set up a Woodstox reader

            XMLInputFactory xmlif = XMLInputFactory2.newInstance();
            XMLStreamReader xmlStreamReader = xmlif.createXMLStreamReader(new FileReader(file));
            boolean isValidForPrice = false;
            boolean insideRowTag = false;
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
                                    System.out.println(TAG_PRINCIPALE + ": " + xmlStreamReader.getElementText());
                                }
                                break;

                            case "Descrizione":
                                if(TAG_PRINCIPALE.equals("DettaglioLinee")) {
                                    String dettaglio = xmlStreamReader.getElementText();
                                    String testoTag = "";
                                    if (dettaglio.contains("/D ") || (dettaglio.contains("/M "))) {
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
                                        String targa = fromTarga.split("\\)")[0];
                                        System.out.println(TAG_PRINCIPALE + ": " + targa.replace(" ", ""));

                                    } else if (testoTag.contains("VF")) {
                                        int position = testoTag.indexOf("VF");
                                        String fromTarga = testoTag.substring(position, testoTag.length()-1);
                                        String targa = fromTarga.split(" ")[0];
                                        System.out.println(TAG_PRINCIPALE + ": " + targa);
                                    }

                                    // Esclude alcune righe in base al loro contenuto
                                    if(!(testoTag.contains("con Iva non incassata") || testoTag.contains("----"))) {
                                        isValidForPrice = true;
                                        System.out.println(TAG_PRINCIPALE + ": " + testoTag);
                                    }
                                }
                                break;
                            case "PrezzoTotale":
                                if(TAG_PRINCIPALE.equals("DettaglioLinee")) {
                                    String testoTag = xmlStreamReader.getElementText();

                                    // Esclude alcune righe in base al loro contenuto
                                    if(isValidForPrice) {
                                        System.out.println(TAG_PRINCIPALE + ": " + testoTag);
                                        float linePrice = Float.parseFloat(testoTag);
                                        this.price += linePrice;


                                    }
                                }
                                break;

                            case "AliquotaIVA":
                                if(TAG_PRINCIPALE.equals("DettaglioLinee")) {
                                    String testoTag = xmlStreamReader.getElementText();

                                    // Esclude alcune righe in base al loro contenuto
                                    if(isValidForPrice) {
                                        System.out.println(TAG_PRINCIPALE + ": " + testoTag);
                                    }
                                }
                                break;

                            case "IdDocumento":
                                if(TAG_PRINCIPALE.equals("DatiOrdineAcquisto")) {
                                    System.out.println(TAG_PRINCIPALE + ": " + xmlStreamReader.getElementText());
                                }
                                break;
                            case "Data": // Oppure NumItem TODO: se non esiste una data utilizzare la data dentro il NumItem
                                if(TAG_PRINCIPALE.equals("DatiOrdineAcquisto")) {
                                    System.out.println(TAG_PRINCIPALE + ": " + xmlStreamReader.getElementText());
                                }
                                break;
                            case "ImportoTotaleDocumento":
                                if(TAG_PRINCIPALE.equals("DatiGeneraliDocumento")) {
                                    System.out.println(TAG_PRINCIPALE + ": " + xmlStreamReader.getElementText());
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

            System.out.println(TAG_PRINCIPALE + ": " + this.price);


            long endTime   = System.nanoTime();
            long totalTime = endTime - startTime;
            logger.info("Done! Time took: {}", totalTime / 1000000000);
        }

    }

