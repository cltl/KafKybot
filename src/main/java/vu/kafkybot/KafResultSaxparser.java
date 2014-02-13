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

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/7/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafResultSaxparser extends DefaultHandler{

    String value;
    String fileName;
    public HashMap<String, ArrayList<KafResult>> tupleMap;


    public KafResultSaxparser() {
        init();
    }

    void init () {
        tupleMap = new HashMap<String, ArrayList<KafResult>>();
        fileName = "";
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

        if (qName.equalsIgnoreCase("coref")) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getQName(i);
                if (name.equalsIgnoreCase("method")) {
                   // method = attributes.getValue(i).trim();
                }
                else if (name.equalsIgnoreCase("threshold")) {
                  //  threshold = attributes.getValue(i).trim();
                }
            }
        }
        else if (qName.equalsIgnoreCase("co-ref-sets")) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getQName(i);
                if (name.equalsIgnoreCase("file")) {

/*
                    orgFileName = attributes.getValue(i).trim();
                    fileName = orgFileName;
                    if (fileName.length()>=namesubstring) {
                        if (namesubstring>-1) {
                            fileName = fileName.substring(0, namesubstring);
                            //  System.out.println("fileName = " + fileName);
                        }
                    }
                    else {
                        System.out.println("fileName = " + fileName);
                    }
*/
                }
            }
        }
        else if (qName.equalsIgnoreCase("co-refs")) {
/*
            corefSet = new CoRefSet();
            // corefSet.setId(orgFileName);
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getQName(i);
                if (name.equalsIgnoreCase("id")) {
                    corefSet.setId(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("lcs")) {
                    corefSet.setLcs(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("score")) {
                    try {
                        double score = Double.parseDouble(attributes.getValue(i).trim());
                        corefSet.setScore(score);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
*/
        }
        else if (qName.equalsIgnoreCase("target")) {
/*
            CorefTarget target = new CorefTarget();
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getQName(i);
                if (name.equalsIgnoreCase("termId")) {
                    target.setTermId(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("docId")) {
                    target.setDocId(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("sentenceId")) {
                    target.setSentenceId(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("word")) {
                    target.setWord(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("synset")) {
                    target.setSynset(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("rank")) {
                    try {
                        double score = Double.parseDouble(attributes.getValue(i).trim());
                        target.setSynsetScore(score);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }
                else if (name.equalsIgnoreCase("corefScore")) {
                    try {
                        double score = Double.parseDouble(attributes.getValue(i).trim());
                        target.setCorefScore(score);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }
            }
            corefSet.addTarget(target);
*/
        }
        value = "";
    }//--startElement


    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("co-refs")) {
/*
            if (corefSet.getTargets().size()>cardinality) {
                //// we only take coref sets with cardinality >1
                if (corefMap.containsKey(fileName)) {
                    ArrayList<CoRefSet> sets = corefMap.get(fileName);
                    sets.add(corefSet);
                    corefMap.put(fileName, sets);
                }
                else {
                    ArrayList<CoRefSet> sets = new ArrayList<CoRefSet>();
                    sets.add(corefSet);
                    corefMap.put(fileName, sets);
                }
            }
            else {
                //    System.out.println("Ignore corefSet = " + corefSet);
            }
*/
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
        // System.out.println("tagValue:"+value);
    }
}
