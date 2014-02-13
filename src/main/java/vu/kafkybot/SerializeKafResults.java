package vu.kafkybot;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/7/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SerializeKafResults {


    /*
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<tuples source="Audi-109-NULL.kaf.step.0.step.1.step.2.step.3.dep.kaf">
  <tuple id="1" profile="dep-obj-pass" sentenceId="s2">
    <!--The windows are taped over .-->
    <event lemma="tape" mention="t21" pos="VBN"/>
    <participant dep="nsubjpass" lemma="window" mention="t19" pos="NNS" role="p2"/>
  </tuple>
  <tuple id="2" profile="dep-obj-pass" sentenceId="s4">
    <!--The windows are taped over .-->
    <event lemma="tape" mention="t43" pos="VBN"/>
    <participant dep="nsubjpass" lemma="window" mention="t41" pos="NNS" role="p2"/>
  </tuple>
</tuples>
     */

    static public void writeMapToStream(HashMap<String, ArrayList<KafResult>> kafResultMap, OutputStream stream)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            Document xmldoc = impl.createDocument(null, "kafkybot-results", null);
            xmldoc.setXmlStandalone(true);
            Element root = xmldoc.getDocumentElement();
            Set keySet = kafResultMap.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                ArrayList<KafResult> results = kafResultMap.get(key);
               // System.out.println("results.size() = " + results.size());
                Element docResult = xmldoc.createElement("tuples");
                docResult.setAttribute("source", key);
                for (int i = 0; i < results.size(); i++) {
                    KafResult kafResult = results.get(i);
                   // System.out.println("kafResult.toString() = " + kafResult.toString());
                    docResult.appendChild(kafResult.toXML(xmldoc));
                }
                root.appendChild(docResult);
            }
            // Serialisation through Tranform.
            DOMSource domSource = new DOMSource(xmldoc);
            TransformerFactory tf = TransformerFactory.newInstance();
            //tf.setAttribute("indent-number", 4);
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT,"yes");
            serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            // serializer.setParameter("format-pretty-print", Boolean.TRUE);
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StreamResult streamResult = new StreamResult(new OutputStreamWriter(stream,"UTF-8"));
            serializer.transform(domSource, streamResult);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
