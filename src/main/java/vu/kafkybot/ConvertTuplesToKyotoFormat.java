package vu.kafkybot;

import eu.kyotoproject.kybotoutput.objects.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 6/26/13
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertTuplesToKyotoFormat {

     /*
     <doc shortname="AFP_ENG_20050715.0342.src.xml.txt.blk.tok.stp.tbf.xml.isi-term.ont.kaf">

     event eid="e14"
     lemma="authority"
     pos="N.NS" target="t230"
     profile_id="Participant_dummy_Pnoun,Participant_Pnoun_dummy"
     score="0.0"
     freq="0"
     domain="administration"
     gran_nr="plural"
     ontology=""
     gran_type="gran_group"
     synset="eng-30-08337324-n"
     rank="0.142765">
</event>
<event eid="e33" lemma="Barakat" pos="N.P" target="t310" profile_id="participant_Nname2,participant_Nname_dummy,participant_Nname3" score="0.0" freq="0" domain="" gran_nr="singular" ontology="" gran_type="" synset="" rank="0.0">
</event>
  <tuples source="19_4.eecb.kaf.step.0.step.1.step.2.ont.kaf.ont.kaf">
    <tuple id="1" profile="Participant_Pnoun_dummy" sentenceId="s2">

    <tuple id="16" profile="Participant_Pnoun_dummy" sentenceId="s1">
      <!--Saints place RB Bush on injured reserve The injury-plagued season of New Orleans Saints running back Reggie Bush is over .-->
      <dummytype lemma="the" mention="t8" pos="DT" tokens="w8"/>
      <Participant
      concept="eng-30-10671042-n"
      confidence="0.841783"
      lemma="reserve"
      mention="t7"
      pos="NN"
      reference="ParticipantHuman"
      tokens="w7"/>
    </tuple>

<co-ref-sets file="TOPIC_33_EVENT_COREFERENCE_CORPUS">
	<co-refs cid="eecb1.0:33_15">
		<target termId="eecb1.0:33/2.eecb.kaf.step.0.step.1.step.2.ont.kaf.ont.kaf.offset.kaf_stanford-parser-en#t127" comment="ask"/>
	</co-refs>
	<co-refs cid="eecb1.0:33_14">
		<target termId="eecb1.0:33/2.eecb.kaf.step.0.step.1.step.2.ont.kaf.ont.kaf.offset.kaf_stanford-parser-en#t122" comment="tell"/>
	</co-refs>
	<co-refs cid="eecb1.0:33_17">
		<target termId="eecb1.0:33/3.eecb.kaf.step.0.step.1.step.2.ont.kaf.ont.kaf.offset.kaf_stanford-parser-en#t442" comment="say"/>
		<target termId="eecb1.0:33/4.eecb.kaf.step.0.step.1.step.2.ont.kaf.ont.kaf.offset.kaf_stanford-parser-en#t31" comment="begin"/>
	</co-refs>
	<co-refs cid="eecb1.0:33_16">
		<target termId="eecb1.0:33/3.eecb.kaf.step.0.step.1.step.2.ont.kaf.ont.kaf.offset.kaf_stanford-parser-en#t313" comment="come"/>
	</co-refs>

      */
    static public void main (String[] args) {
        String pathToTuplesFile = "/Tools/kafkybot.v.0.1/test/Kybot_output_soh_LTP_onECB_24July2013.ont.dep.kaf.sem.tpl";
        String firstElement = "Participant";
        String nameSpace = "eecb1.0:";
        String processName = "stanford-parser-en";
        String globalTopicName = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--tuple-file") && (i+1)<args.length) {
                pathToTuplesFile = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--element-name") && (i+1)<args.length) {
                firstElement = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--name-space") && (i+1)<args.length) {
                nameSpace = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--process-name") && (i+1)<args.length) {
                processName = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--topic-name") && (i+1)<args.length) {
                globalTopicName = args[i+1];
            }
        }
        if (!pathToTuplesFile.isEmpty()) {
            try {
                HashMap<String, ArrayList<KafResult>> topicMap = new HashMap<String, ArrayList<KafResult>>();
                String pathToKyotoEventFile = firstElement+".xml";
                FileOutputStream fos = new FileOutputStream(pathToKyotoEventFile);
                String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
                str += "<kybotOut>\n";
                fos.write(str.getBytes());
                TupleSaxParser tupleSaxParser = new TupleSaxParser();
                tupleSaxParser.parseFile(pathToTuplesFile);
                Set keySet = tupleSaxParser.tupleMap.keySet();
                Iterator keys = keySet.iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    System.out.println("key = " + key);
                    //19_4.eecb.kaf.step.0.step.1.step.2.ont.kaf.ont.kaf

                    int idx = key.indexOf("_");
                    String topicName = key;
                    String fileName = key;
                    if (idx>-1) {
                        topicName = key.substring(0, idx);
                        fileName = key.substring(idx+1);
                    }
                    if (!globalTopicName.isEmpty()) {
                       topicName = globalTopicName;
                    }
                    ArrayList<KafResult> kafResults = tupleSaxParser.tupleMap.get(key);
                    System.out.println("kafResults.size() = " + kafResults.size());
                    for (int i = 0; i < kafResults.size(); i++) {
                        KafResult kafResult = kafResults.get(i);
                        for (int j = 0; j < kafResult.getChildren().size(); j++) {
                            TupleElement tupleElement =  kafResult.getChildren().get(j);
                            if (tupleElement.getName().equals(firstElement)) {
                                String id = nameSpace+topicName+"/"+ fileName+processName+"#"+tupleElement.getMention();
                                tupleElement.setMention(id);
                            }
                        }
                        addToTopicMap(topicMap, kafResult, topicName);
                    }
                }
                keySet = topicMap.keySet();
                keys = keySet.iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    System.out.println("key = " + key);
                    //19
                    //file="TOPIC_33_EVENT_COREFERENCE_CORPUS
                    str = "<doc shortname=\"TOPIC_"+key+"_EVENT_COREFERENCE_CORPUS\">\n";
                    fos.write(str.getBytes());
                    ArrayList<KafResult> kafResults = topicMap.get(key);
                    System.out.println("kafResults.size() = " + kafResults.size());
                    for (int i = 0; i < kafResults.size(); i++) {
                        KafResult kafResult = kafResults.get(i);
                        for (int j = 0; j < kafResult.getChildren().size(); j++) {
                            TupleElement tupleElement =  kafResult.getChildren().get(j);
                            if (tupleElement.getName().equals(firstElement)) {
                                KybotEvent kybotEvent = new KybotEvent();
                                kybotEvent.setEventId(kafResult.getId());
                                kybotEvent.setSynsetId(tupleElement.getConcept());
                                kybotEvent.setSynsetScore(tupleElement.getConfidence());
                                kybotEvent.setOntType(tupleElement.getReference());
                                kybotEvent.setPos(tupleElement.getPos());
                                kybotEvent.setLemma(tupleElement.getLemma());
                                kybotEvent.setTarget(tupleElement.getMention());
                                kybotEvent.setGran_nr((tupleElement.getGran_nr()));
                                kybotEvent.setProfileId(kafResult.getProfileName());
                                kybotEvent.setSentenceId(kafResult.getSentenceId());
                                str = "\t"+kybotEvent.toXmlString();
                                fos.write(str.getBytes());
                            }
                        }
                    }
                    str = "</doc>\n";
                    fos.write(str.getBytes());
                }
                str = "</kybotOut>\n";
                fos.write(str.getBytes());
                //fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    static void addToTopicMap (HashMap<String, ArrayList<KafResult>> topicMap, KafResult kafResult, String topic) {
        if (topicMap.containsKey(topic)) {
            ArrayList<KafResult> kafResults = topicMap.get(topic);
            kafResults.add(kafResult);
            topicMap.put(topic, kafResults);
        }
        else {
            ArrayList<KafResult> kafResults = new ArrayList<KafResult>();
            kafResults.add(kafResult);
            topicMap.put(topic, kafResults);
        }

    }


}
