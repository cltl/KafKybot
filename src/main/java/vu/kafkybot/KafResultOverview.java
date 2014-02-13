package vu.kafkybot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/7/13
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafResultOverview {


    static public void makeOverviewFile (HashMap<String, ArrayList<KafResult>> kafResultMap, FileOutputStream fos) throws IOException {

        HashMap<String,  ArrayList<KafResult>> events = new HashMap<String, ArrayList<KafResult>>();
        Set keySet = kafResultMap.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            ArrayList<KafResult> results = kafResultMap.get(key);
            for (int i = 0; i < results.size(); i++) {
                KafResult kafResult = results.get(i);
                for (int j = 0; j < kafResult.getChildren().size(); j++) {
                    TupleElement tupleElement = kafResult.getChildren().get(j);
                    String keyLemma = tupleElement.getName()+":"+tupleElement.getLemma()+":"+tupleElement.getRole();
                    if (events.containsKey(keyLemma)) {
                        ArrayList<KafResult> lemmaResults = events.get(keyLemma);
                        lemmaResults.add(kafResult);
                        events.put(keyLemma, lemmaResults);
                    }
                    else {
                        ArrayList<KafResult> lemmaResults = new ArrayList<KafResult>();
                        lemmaResults.add(kafResult);
                        events.put(keyLemma, lemmaResults);
                    }
                }
            }
        }
        keySet = events.keySet();
        keys = keySet.iterator();


        TreeSet sorter = new TreeSet();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            sorter.add(key);

        }
        keys = sorter.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            ArrayList<KafResult> results = events.get(key);
            String str = key+"\t"+results.size();
            str += getsortedResultCounts(key, results);
            fos.write(str.getBytes());
        }
    }

    static public String getResultCounts (String keyLemma, ArrayList<KafResult> kafResults) {
        String str = "";
        HashMap<String, Integer> relCounts = new HashMap<String, Integer>();
        String [] fields = keyLemma.split(":");
        if (fields.length>=2) {
            String name = fields[0];
            String lemma = fields[1];
            for (int i = 0; i < kafResults.size(); i++) {
                KafResult kafResult = kafResults.get(i);
                for (int j = 0; j < kafResult.getChildren().size(); j++) {
                    TupleElement tupleElement = kafResult.getChildren().get(j);
                    if (!tupleElement.getName().equals(name) && !tupleElement.getLemma().equals(lemma)) {
                       String targetLemma = tupleElement.getName()+":"+tupleElement.getLemma()+":"+tupleElement.getRole();
                        if (relCounts.containsKey(targetLemma)) {
                            Integer cnt = relCounts.get(targetLemma);
                            cnt++;
                            relCounts.put(targetLemma, cnt);
                        }
                        else {
                            relCounts.put(targetLemma, 1);
                        }
                    }
                }
            }
            Set keySet = relCounts.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Integer cnt = relCounts.get(key);
                str += "\t"+key+"\t"+cnt.toString();
            }
        }
        str += "\n";
        return str;
    }

    static public String getsortedResultCounts (String keyLemma, ArrayList<KafResult> kafResults) {
        class lemmaCount {
            public String lemma;
            public Integer cnt;

            public lemmaCount () {
                lemma = "";
                cnt = 0;
            }
        }
        String str = "";
        HashMap<String, lemmaCount> relCounts = new HashMap<String, lemmaCount>();
        String [] fields = keyLemma.split(":");
        if (fields.length>=2) {
            String name = fields[0];
            String lemma = fields[1];
            for (int i = 0; i < kafResults.size(); i++) {
                KafResult kafResult = kafResults.get(i);
                for (int j = 0; j < kafResult.getChildren().size(); j++) {
                    TupleElement tupleElement = kafResult.getChildren().get(j);
                    if (!tupleElement.getName().equals(name) && !tupleElement.getLemma().equals(lemma)) {
                       String targetLemma = tupleElement.getName()+":"+tupleElement.getLemma()+":"+tupleElement.getRole();
                        if (relCounts.containsKey(targetLemma)) {
                            lemmaCount cnt = relCounts.get(targetLemma);
                            cnt.cnt++;
                            relCounts.put(targetLemma, cnt);
                        }
                        else {
                            lemmaCount cnt = new lemmaCount();
                            cnt.cnt= 1;
                            cnt.lemma = targetLemma;
                            relCounts.put(targetLemma, cnt);
                        }
                    }
                }
            }
            TreeSet sorter = new TreeSet(
                    new Comparator() {
                        public int compare(Object a, Object b) {
                            lemmaCount itemA = (lemmaCount) a;
                            lemmaCount itemB = (lemmaCount) b;
                            Integer nA = new Integer(itemA.cnt);
                            Integer nB = new Integer(itemB.cnt);
                            if (nB.equals(nA)) // We force equal frequencies to be inserted
                            {
                                return -1;
                            } else {
                                return (nB.compareTo(nA));
                            }
                        }
                    }

            );
            Set keySet = relCounts.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                lemmaCount cnt = relCounts.get(key);
                sorter.add(cnt);

            }
            keys = sorter.iterator();
            while (keys.hasNext()) {
                lemmaCount cnt = (lemmaCount) keys.next();
                str += "\t"+cnt.lemma+"\t"+cnt.cnt.toString();

            }
        }
        str += "\n";
        return str;
    }

}
