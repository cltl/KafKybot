package vu.kafkybot;

import vu.tripleevaluation.objects.Triple;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 6/26/13
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertTuplesToTriples {


    static public void main (String[] args) {
        String pathToTuplesFile = "";
        String firstElement = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--tuple-file") && (i+1)<args.length) {
                pathToTuplesFile = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--first-element") && (i+1)<args.length) {
                firstElement = args[i+1];
            }
        }
        if (!pathToTuplesFile.isEmpty()) {
            try {
                String pathToTripleFile = pathToTuplesFile+".trp";
                FileOutputStream fos = new FileOutputStream(pathToTripleFile);
                String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
                str += "<triples>\n";
                fos.write(str.getBytes());
                TupleSaxParser tupleSaxParser = new TupleSaxParser();
                tupleSaxParser.parseFile(pathToTuplesFile);
                Set keySet = tupleSaxParser.tupleMap.keySet();
                Iterator keys = keySet.iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    System.out.println("key = " + key);
                    ArrayList<KafResult> kafResults = tupleSaxParser.tupleMap.get(key);
                    System.out.println("kafResults.size() = " + kafResults.size());
                    for (int i = 0; i < kafResults.size(); i++) {
                        KafResult kafResult = kafResults.get(i);
                        for (int j = 0; j < kafResult.getChildren().size(); j++) {
                            TupleElement tupleElement =  kafResult.getChildren().get(j);
                            if (tupleElement.getName().equals(firstElement)) {
                                for (int k = 0; k < kafResult.getChildren().size(); k++) {
                                    if ((k!=j)) {
                                        Triple triple = new Triple();
                                        triple.setTripleId(key+"#"+kafResult.getSentenceId()+"#"+kafResult.getId());
                                        triple.setProfileId(kafResult.getProfileName());
                                        triple.setProfileConfidence(kafResult.getProfileConfidence());
                                        triple.setElementFirstIds(tupleElement.getTokens());
                                        triple.setElementFirstComment(tupleElement.getLemma());
                                        triple.setElementFirstLabel(tupleElement.getName());
                                        TupleElement tupleElementOther = kafResult.getChildren().get(k);
                                        triple.setElementSecondIds(tupleElementOther.getTokens());
                                        triple.setElementSecondLabel(tupleElementOther.getName());
                                        triple.setElementSecondComment(tupleElementOther.getLemma());
                                        triple.setRelation(tupleElementOther.getRole());
                                        fos.write(triple.toString().getBytes());
                                    }
                                }
                            }
                        }
                    }
                }
                str = "</triples>\n";
                fos.write(str.getBytes());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }


}
