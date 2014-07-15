package vu.kafkybot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by piek on 3/5/14.
 */
public class TuplesToStorylines {
    /**
     * <?xml version="1.0"?>
     <file_events>
     <event filename="abandonPrixAFP_ENG_19950421.0411" date="199504" author="Suzuki"/>
     <event filename="abandonplanAFP_ENG_19961022.0488" date="199610" author="Saab"/>
     <event filename="abandonplanAFP_ENG_20030620.0129" date="200306" author="Volkswagen"/>
     */

    static public void main (String[] args) {
//        Calendar.DAY_OF_MONTH
//        Calendar.DAY_OF_YEAR
        try {
            HashMap<String, ArrayList<String>> actorPron = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<String>> actorOther = new HashMap<String, ArrayList<String>>();
            HashMap<String, Integer> eventPronCount = new HashMap<String, Integer>();
            HashMap<String, Integer> eventOtherCount = new HashMap<String, Integer>();
            String pathToTupleFolder = "/Users/piek/Desktop/Thomese/Thomese_book_opener_nwr_dep";
            String extension = ".tpl";
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if ((arg.equals("--tpl-folder")) && args.length>i+1) {
                    pathToTupleFolder = args[i+1];
                }
                else if ((arg.equals("--extension")) && args.length>i+1) {
                    extension = args[i+1];
                }
            }
            FileOutputStream foscnt = new FileOutputStream(pathToTupleFolder+"/"+"events-counts.xml") ;
            FileOutputStream fosproncnt = new FileOutputStream(pathToTupleFolder+"/"+"pronevents-counts.xml") ;
            FileOutputStream fos = new FileOutputStream(pathToTupleFolder+"/"+"events.xml") ;
            FileOutputStream fospron = new FileOutputStream(pathToTupleFolder+"/"+"pronevents.xml") ;
            String str = "<?xml version=\"1.0\"?>\n" +
                    "     <file_events>\n";
            fos.write(str.getBytes());
            fospron.write(str.getBytes());

            File folder = new File(pathToTupleFolder);
            if (folder.isDirectory()) {
                ArrayList<String> files = Util.makeRecursiveFileListAll(pathToTupleFolder, extension);
                for (int f = 0; f < files.size(); f++) {
                    File file = new File(files.get(f));
                    if (!file.isDirectory()) {
                            TupleSaxParser tupleSaxParser = new TupleSaxParser();
                            tupleSaxParser.parseFile(file.getAbsolutePath());
                            Set keySet = tupleSaxParser.tupleMap.keySet();
                            Iterator keys = keySet.iterator();
                            while (keys.hasNext()) {
                                String source = (String) keys.next();
                                ArrayList<KafResult> tuples = tupleSaxParser.tupleMap.get(source);
                                for (int i = 0; i < tuples.size(); i++) {
                                    KafResult kafResult = tuples.get(i);
                                    /*

    <tuple id="44" profile="dep-subj-1-other" profileConfidence="50" sentenceId="22">
      <!--Het zou net zo goed een hotelkamer kunnen zijn in Emmen of Middelburg waar ik na mijn lezing uit eigen werk door de liefhebbende organisatie ben ondergebracht .-->
      <event:other concept="nld-21-d_v-4288-v" confidence="0.0" lemma="zullen" mention="t374" pos="V" reference="vn:want-32.1" tokens="w_367"/>
      <participant dep="hd/su" lemma="het" mention="t373" pos="Q" role="a0" tokens="w_366"/>
    </tuple>
                                     */
                                    String page = source;
                                    int idx = source.lastIndexOf(".tok.pos.");
                                    int idx_start = source.lastIndexOf("_");
                                    if (idx_start>-1 && idx>idx_start) {
                                        page = page.substring(idx_start+1, idx);
                                    }
                                    if (page.length()==1) {
                                        page = "0"+page;
                                    }
                                    //FileThomese_book_opener_nwr_dep_page_12.tok.pos.pol.ner.const.dep.opi.kaf.wordnet.kaf.event.kaf.dep
                                    String eventType = "";
                                    String actor = "";
                                    String sentenceId = kafResult.getSentenceId();
                                    int sentenceInt = Integer.parseInt(sentenceId);
                                    int month = sentenceInt%12;
                                    if (month==0) {month++;}
                                   // System.out.println("sentenceInt = " + sentenceInt);
                                   // System.out.println("month = " + month);
                                    String monthString = String.valueOf(month);

                                    if (month<10) {
                                        monthString = "0"+monthString;
                                    }
                                    for (int q = 0; q < kafResult.getChildren().size(); q++) {
                                        TupleElement tupleElement = kafResult.getChildren().get(q);
                                        if (tupleElement.getName().startsWith("event:other")) {
                                            eventType = tupleElement.getLemma();
                                        }
                                    }
                                    if (!eventType.isEmpty()) {
                                        for (int q = 0; q < kafResult.getChildren().size(); q++) {
                                            TupleElement tupleElement = kafResult.getChildren().get(q);
                                            if (tupleElement.getName().equalsIgnoreCase("participant")) {
                                                actor = tupleElement.getLemma();
                                                str = "<event filename=\""+eventType+"P"+page+"\"";
                                                str += " date=\""+"20"+page+monthString+"\"";
                                                //str += " date=\""+page+"00"+sentenceId+"\"";
                                                //str += " date=\""+"2010"+page+"\"";
                                                //str += " date=\""+"20"+page+"01"+"\"";
                                                str += " author=\""+actor+"\"";
                                                str += "/>\n";
                                                if (actor.equalsIgnoreCase("ik") ||
                                                        actor.equalsIgnoreCase("jij") ||
                                                        actor.equalsIgnoreCase("je") ||
                                                        actor.equalsIgnoreCase("hij") ||
                                                        actor.equalsIgnoreCase("zij") ||
                                                        actor.equalsIgnoreCase("ze") ||
                                                        actor.equalsIgnoreCase("wij") ||
                                                        actor.equalsIgnoreCase("we") ||
                                                        actor.equalsIgnoreCase("het") ||
                                                        actor.equalsIgnoreCase("hen") ||
                                                        actor.equalsIgnoreCase("mezelf") ||
                                                        actor.equalsIgnoreCase("wie") ||
                                                        actor.equalsIgnoreCase("me") ||
                                                        actor.equalsIgnoreCase("iedereen") ||
                                                        actor.equalsIgnoreCase("iemand") ||
                                                        actor.equalsIgnoreCase("niemand") ||
                                                        actor.equalsIgnoreCase("niks") ||
                                                        actor.equalsIgnoreCase("die") ||
                                                        actor.equalsIgnoreCase("elkaar") ||
                                                        actor.equalsIgnoreCase("dit") ||
                                                        actor.equalsIgnoreCase("dat") ||
                                                        actor.equalsIgnoreCase("hem") ||
                                                        actor.equalsIgnoreCase("zich") ||
                                                        actor.equalsIgnoreCase("mij") ||
                                                        actor.equalsIgnoreCase("ons")
                                                        ) {
                                                    if (eventPronCount.containsKey(eventType)) {
                                                        Integer cnt = eventPronCount.get(eventType);
                                                        cnt++;
                                                        eventPronCount.put(eventType,cnt);
                                                    }
                                                    else {
                                                        eventPronCount.put(eventType,1);
                                                    }
                                                    if (actorPron.containsKey(actor)) {
                                                        ArrayList<String> cnt = actorPron.get(actor);
                                                        cnt.add(str);
                                                        actorPron.put(actor,cnt);
                                                    }
                                                    else {
                                                        ArrayList<String> cnt = new ArrayList<String>();
                                                        cnt.add(str);
                                                        actorPron.put(actor,cnt);
                                                    }
                                                    //fospron.write(str.getBytes());

                                                }
                                                else {
                                                    if (eventOtherCount.containsKey(eventType)) {
                                                        Integer cnt = eventOtherCount.get(eventType);
                                                        cnt++;
                                                        eventOtherCount.put(eventType,cnt);
                                                    }
                                                    else {
                                                        eventOtherCount.put(eventType,1);
                                                    }
                                                    if (actorOther.containsKey(actor)) {
                                                        ArrayList<String> cnt = actorOther.get(actor);
                                                        cnt.add(str);
                                                        actorOther.put(actor,cnt);
                                                    }
                                                    else {
                                                        ArrayList<String> cnt = new ArrayList<String>();
                                                        cnt.add(str);
                                                        actorOther.put(actor,cnt);
                                                    }

                                                    //fos.write(str.getBytes());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
            }
            Set keySet = actorOther.keySet();
            Iterator  keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                ArrayList<String> events = actorOther.get(key);
                if (events.size()>0) {
                    for (int i = 0; i < events.size(); i++) {
                        String s = events.get(i);
                        fos.write(s.getBytes());
                    }
                }
            }
            str = "</file_events>\n";
            fos.write(str.getBytes());
            fos.close();

            keySet = actorPron.keySet();
            keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                ArrayList<String> events = actorPron.get(key);
                if (events.size()>1) {
                    for (int i = 0; i < events.size(); i++) {
                        String s = events.get(i);
                        fospron.write(s.getBytes());
                    }
                }
            }
            str = "</file_events>\n";
            fospron.write(str.getBytes());
            fospron.close();

            keySet = eventOtherCount.keySet();
            keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Integer cnt = eventOtherCount.get(key);
                str = cnt+"\t"+key+"\n";
                foscnt.write(str.getBytes());
            }
            keySet = eventPronCount.keySet();
            keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Integer cnt = eventPronCount.get(key);
                str = cnt+"\t"+key+"\n";
                fosproncnt.write(str.getBytes());
            }
            foscnt.close();
            fosproncnt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
