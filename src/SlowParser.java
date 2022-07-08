import org.codehaus.stax2.XMLInputFactory2;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;

public class SlowParser {
    public static void main(String[] args) throws Exception {
        SlowParser xsp =
                new SlowParser();
        String filename = "C:\\Users\\davide.piana\\IdeaProjects\\parserFatturaElettronica\\src\\test.xml";
        xsp.fastParseFile(filename);
    }

    public void parseFile(String filename) throws Exception {
        long starttime = System.currentTimeMillis();
        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = parserFactory.newDocumentBuilder();
    }

    public void fastParseFile(String filename) {
        XMLInputFactory2 xmlif;
        try {
            xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
            xmlif.setProperty(
                    XMLInputFactory.
                            IS_REPLACING_ENTITY_REFERENCES,
                    Boolean.FALSE);
            xmlif.setProperty(
                    XMLInputFactory.
                            IS_SUPPORTING_EXTERNAL_ENTITIES,
                    Boolean.FALSE);
            xmlif.setProperty(
                    XMLInputFactory.
                            IS_COALESCING,
                    Boolean.FALSE);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Starting to parse " + filename);
        System.out.println("");





        long starttime = System.currentTimeMillis();
        int elementCount = 0;
        int filteredCharCount = 0;
    }



}