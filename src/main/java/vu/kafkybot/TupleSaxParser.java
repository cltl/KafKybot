package vu.kafkybot;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 6/26/13
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 <kafkybot-results>
 <tuples source="Pandarchief_6789_23012006_IT3.pdf.txt.kaf.wordnet.kaf.ont.kaf">
 <tuple id="1" profile="pos-v-n" profileConfidence="10" sentenceId="330">
 <!--Het pijpwerk gezien vanaf de noordzijde .-->
 <object lemma="noordzijde" mention="t_4827" pos="N" tokens="w_4827"/>
 <functie lemma="zien" mention="t_4824" pos="V" tokens="w_4824"/>
 </tuple>
 <tuple id="2" profile="pos-n-v" profileConfidence="10" sentenceId="335">
 <!--Een beschrijving van het pijpwerk van het manuaal ( C t/m f3 ) is in volgorde vanaf het front ; IQ Prestant 8 ' C t/m E open , gekropte houten pijpen geplaatst op een stok achter de middentoren .-->
 <functie lemma="zijn" mention="t_4904" pos="V" tokens="w_4904"/>
 <object lemma="beschrijving" mention="t_4892" pos="N" tokens="w_4892"/>
 </tuple>
 */
public class TupleSaxParser extends DefaultHandler{

    String value;
    String fileName;
    private String tupleSource;
    private ArrayList<KafResult> kafResultsArray;
    private KafResult kafResult;
    private TupleElement tupleElement;
    public HashMap<String, ArrayList<KafResult>> tupleMap;


    public TupleSaxParser() {
        init();
    }

    void init () {
        kafResultsArray = new ArrayList<KafResult>();
        tupleSource = "";
        tupleMap = new HashMap<String, ArrayList<KafResult>>();
        fileName = "";
        kafResult = new KafResult();
        tupleElement = new TupleElement();
    }

    public void parseFile(String filePath) {
        String myerror = "";
        System.out.println("filePath = " + filePath);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            InputSource inp = new InputSource (new FileReader(filePath));
            parser.parse(inp, this);
            System.out.println("kafResultMap.size() = " + tupleMap.size());
        } catch (SAXParseException err) {
            myerror = "\n** Parsing error" + ", line " + err.getLineNumber()
                    + ", uri " + err.getSystemId();
            myerror += "\n" + err.getMessage();
            System.out.println("myerror = " + myerror);
        } catch (SAXException e) {
            Exception x = e;
            if (e.getException() != null)
                x = e.getException();
            myerror += "\nSAXException --" + x.getMessage();
            System.out.println("myerror = " + myerror);
        } catch (Exception eee) {
            eee.printStackTrace();
            myerror += "\nException --" + eee.getMessage();
            System.out.println("myerror = " + myerror);
        }
    }//--c



    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {
        /**
         * <tuples source="bus-accident.ont.dep.kaf">
         * <tuple id="2" profile="pos-n-v" profileConfidence="10" sentenceId="335">
         <!--Een beschrijving van het pijpwerk van het manuaal ( C t/m f3 ) is in volgorde vanaf het front ; IQ Prestant 8 ' C t/m E open , gekropte houten pijpen geplaatst op een stok achter de middentoren .-->
         <functie lemma="zijn" mention="t_4904" pos="V" tokens="w_4904"/>
         <object lemma="beschrijving" mention="t_4892" pos="N" tokens="w_4892"/>
         </tuple>


         <tuple id="3" profile="agent-1-sem" sentenceId="s1">
         <!--Several people died and 27 people were injured on Sunday when a private charter tour bus coming down a mountain road collided with an SUV and another car .-->
         <event concept="eng-30-00358431-v" confidence="0.662059" lemma="die" mention="t3" pos="VBD" tokens="w3"/>
         <participant concept="eng-30-08160276-n" confidence="0.0567295" lemma="people" mention="t2" pos="NNS" reference="ExtendedDnS.owl#social-object" role="agent" tokens="w2"/>
         </tuple>
         */
        if (qName.equalsIgnoreCase("tuples")) {
            kafResultsArray = new ArrayList<KafResult>();
            tupleSource = "";
            tupleSource = attributes.getValue("source");
        }
        else if (qName.equalsIgnoreCase("tuple")) {
                kafResult = new KafResult();
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getQName(i);
                    if (name.equalsIgnoreCase("id")) {
                        kafResult.setId(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("sentenceid")) {
                        kafResult.setSentenceId(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("relation")) {
                        kafResult.setRelation(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("comment")) {
                        kafResult.setComment(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("profile")) {
                        kafResult.setProfileName(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("profileconfidence")) {
                        try {
                            int conf = Integer.parseInt(attributes.getValue(i).trim());
                            kafResult.setProfileConfidence(conf);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
           // System.out.println("kafResult = " + kafResult.getSentenceId());
            }
            else {
                /*<event concept="eng-30-00358431-v" confidence="0.662059" lemma="die" mention="t3" pos="VBD" tokens="w3"/>
                */

            /*
                    this.name = "";
        this.depFrom = "";
        this.depTo = "";
        this.depRelation = "";
        this.concept = "";
        this.reference = "";
        this.confidence = 0;
        this.lemma = "";
        this.tokens = new ArrayList<String>();
        this.mention = "";
        this.pos = "";
        this.role = "";
             */
                tupleElement = new TupleElement();
                tupleElement.setName(qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getQName(i);
                    if (name.equalsIgnoreCase("lemma")) {
                        tupleElement.setLemma(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("concept")) {
                        tupleElement.setConcept(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("role")) {
                        tupleElement.setRole(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("depfrom")) {
                        tupleElement.setDepFrom(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("depto")) {
                        tupleElement.setDepTo(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("deprelation")) {
                        tupleElement.setDepRelation(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("reference")) {
                        tupleElement.setReference(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("gran_nr")) {
                        tupleElement.setGran_nr(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("mention")) {
                        tupleElement.setMention(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("pos")) {
                        tupleElement.setPos(attributes.getValue(i).trim());
                    }
                    else if (name.equalsIgnoreCase("tokens")) {
                        String [] tokenRange = attributes.getValue(i).trim().split(";");
                        tupleElement.setTokens(tokenRange);
                    }
                    else if (name.equalsIgnoreCase("confidence")) {
                        double conf = Double.parseDouble(attributes.getValue(i).trim());
                        tupleElement.setConfidence(conf);
                    }
                }
                kafResult.addChildren(tupleElement);
            }
            value = "";
    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("tuples")) {
            if (!tupleSource.isEmpty()) {
                tupleMap.put(tupleSource, kafResultsArray);
               // kafResultsArray = new ArrayList<vu.kafkybot.KafResult>();
            }
        }
        else if (qName.equalsIgnoreCase("tuple")) {
            kafResultsArray.add(kafResult);
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }
    
    static public void main (String[] args) {
        TupleSaxParser tupleSaxParser = new TupleSaxParser();
        tupleSaxParser.parseFile("/Tools/kafkybot.v.0.1/example/bus-accident.ont.dep.kaf.sem.trp");
        System.out.println("tupleSaxParser.tupleMap.size() = " + tupleSaxParser.tupleMap.size());
        Set keySet = tupleSaxParser.tupleMap.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            System.out.println("key = " + key);
            ArrayList<KafResult> kafResults = tupleSaxParser.tupleMap.get(key);
            System.out.println("kafResults.size() = " + kafResults.size());
            for (int i = 0; i < kafResults.size(); i++) {
                KafResult kafResult = kafResults.get(i);
            //    System.out.println("kafResult.toXML() = " + kafResult.toString());
            }
        }
    }
}
