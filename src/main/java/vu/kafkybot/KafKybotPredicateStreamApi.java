package vu.kafkybot;

import eu.kyotoproject.kaf.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 9/10/13
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafKybotPredicateStreamApi {

    static final String layer = "predicate";
    static final String name = "vua-kybot-predicate-role-detection";
    static final String version = "1.0";

    static public void main (String[] args) {
        HashMap<String, ArrayList<KafResult>> kafResultMap = new HashMap<String, ArrayList<KafResult>>();
       // String pathToKafFile = "//Tools/kafkybot.v.0.1/cars/Volkswagen-48-c6577c99c0be5cef4793cf54defb55e345fbe965.kaf.step.0.step.1.step.2.step.3.event.kaf";
        String pathToKafFile = "//Tools/kafkybot.v.0.1/cars/Volkswagen-48-c6577c99c0be5cef4793cf54defb55e345fbe965.kaf.step.0.step.1.step.2.step.3.event.kaf";
        String pathToProfiles = "/Tools/kafkybot.v.0.1/profiles/car-profiles-dep-other-event-subj-obj.txt";
        String format = "NAF";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equals("--kaf-file")) && args.length>i+1) {
                pathToKafFile = args[i+1];
            }
            else if ((arg.equals("--profiles")) && args.length>i+1) {
                pathToProfiles = args[i+1];
            }
            else if ((arg.equals("--format")) && args.length>i+1) {
                format = args[i+1];
            }
        }
        File kafFile = new File(pathToKafFile);
        KafSaxParser kafSaxParser = new KafSaxParser();
        processKafFile(kafSaxParser, kafFile, pathToProfiles, format);
        if (format.equalsIgnoreCase("kaf")) {
            kafSaxParser.writeKafToStream(System.out);
        }
        else if (format.equalsIgnoreCase("naf")) {
            kafSaxParser.writeNafToStream(System.out);
        }
        else if (format.equalsIgnoreCase("nafrdf")) {
            kafSaxParser.writeNafRdfToStream(System.out);
        }
    }

    static public void processKafFile (KafSaxParser kafSaxParser, File kafFile, String pathToProfiles, String format ) {
        HashMap<String, ArrayList<KafResult>> kafResultMap = new HashMap<String, ArrayList<KafResult>>();
        kafSaxParser.parseFile(kafFile);
        kafSaxParser.getKafMetaData().setFilename(kafFile.getAbsolutePath());
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(System.currentTimeMillis());
        String strdate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date != null) {
            strdate = sdf.format(date.getTime());
        }
        kafSaxParser.getKafMetaData().addLayer(layer, name, version, strdate);

        if (kafFile.exists()) {
            kafResultMap = KafKybot.ApplyProfilesToKafFile(kafSaxParser, pathToProfiles);
            Set keySet = kafResultMap.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();  //// key is the document identifier
                ArrayList<KafResult> results = kafResultMap.get(key);
                HashMap<String, KafEvent> kafEventMap = new HashMap<String, KafEvent>();
                for (int i = 0; i < results.size(); i++) {
                    KafResult kafResult = results.get(i);
                    for (int j = 0; j < kafResult.getChildren().size(); j++) {
                        TupleElement tupleElement = kafResult.getChildren().get(j);
                        //System.out.println("tupleElement.getMention() = " + tupleElement.getMention());
                        //System.out.println("tupleElement.getConcept() = " + tupleElement.getConcept());
                        //System.out.println("tupleElement.getConfidence() = " + tupleElement.getConfidence());
                        if (tupleElement.getName().toLowerCase().startsWith("event")) {
                            String eventId = "";
                            if (!format.equalsIgnoreCase("nafrdf")) {
                                eventId  = "pr:"+key+":"+kafResult.getSentenceId()+":"+tupleElement.getMention();
                            }
                            else {
                                eventId  = "&docId;"+kafResult.getSentenceId()+":"+tupleElement.getMention();
                            }
                            KafEvent kafEvent = new KafEvent();
                            if (kafEventMap.containsKey(eventId)) {
                                kafEvent = kafEventMap.get(eventId);
                            }
                            else {
                                kafEvent.setId(eventId);
                                kafEvent.setComponentType("event");
                                if (!kafResult.getSentenceId().isEmpty())  {
                                    kafEvent.setSentenceId(kafResult.getSentenceId());
                                }
                                if (!tupleElement.getName().isEmpty())  {
                                    KafSense kafSense = new KafSense();
                                    String sense = tupleElement.getName();
                                    int idx = sense.indexOf(":");
                                    if (idx >-1) {
                                        sense = sense.substring(idx+1);
                                    }
                                    kafSense.setSensecode(sense);
                                    kafSense.setRefType(tupleElement.getReference());
                                    kafSense.setResource(tupleElement.getProfileId());
                                    kafEvent.addExternalReferences(kafSense);
                                }
                                if (!tupleElement.getReference().isEmpty())  {
                                    KafSense kafSense = new KafSense();
                                    String sense = tupleElement.getReference();
                                    String resource = "";
                                    int idx = sense.indexOf(":");
                                    if (idx >-1) {
                                        resource = sense.substring(0, idx-1);
                                        sense = sense.substring(idx+1);
                                    }
                                    kafSense.setSensecode(sense);
                                    kafSense.setResource(resource);
                                    kafSense.setRefType(tupleElement.getReference());
                                    kafEvent.addExternalReferences(kafSense);
                                }
                                if (!tupleElement.getConcept().isEmpty()) {
                                    KafSense kafSense = new KafSense();
                                    kafSense.setSensecode(tupleElement.getConcept());
                                    kafSense.setConfidence(tupleElement.getConfidence());
                                    kafSense.setResource("wn:"+tupleElement.getConcept().substring(0,6));
                                    kafEvent.addExternalReferences(kafSense);
                                }
                                kafEvent.setSynsetId(tupleElement.getConcept());
                                kafEvent.setSynsetConfidence(tupleElement.getConfidence());
                                kafEvent.addSpan(tupleElement.getMention());
                                if (tupleElement.geoCountryObjects.size()>0) {
                                    //  System.out.println("geoCountryObjects = " + geoCountryObjects.size());
                                    for (int g = 0; g < tupleElement.geoCountryObjects.size(); g++) {
                                        GeoCountryObject geoCountryObject = tupleElement.geoCountryObjects.get(g);
                                        if (!Util.overlappingSpans(geoCountryObject.getSpans(), kafEvent.getParticipants())) {
                                            String participantId = "";
                                            if (!format.equalsIgnoreCase("nafrdf")) {
                                                participantId = "rl:"+key+":"+geoCountryObject.getcId();
                                            }
                                            else {
                                                participantId = "&docId;"+geoCountryObject.getcId();
                                            }
                                            KafParticipant kafParticipant = new KafParticipant();
                                            kafParticipant.setId(participantId);
                                            kafParticipant.setRole("l2");
                                            if (geoCountryObject.getExternalReferences().size()>0) {
                                                kafParticipant.setExternalReferences(geoCountryObject.getExternalReferences());
                                            }
                                            kafParticipant.setElementName("country");
                                            kafParticipant.setSpans(geoCountryObject.getSpans());
                                            kafEvent.addParticipant(kafParticipant);

                                        }
                                    }
                                }
                                if (tupleElement.geoPlaceObjects.size()>0) {
                                    for (int g = 0; g < tupleElement.geoPlaceObjects.size(); g++) {
                                        GeoPlaceObject geoPlaceObject = tupleElement.geoPlaceObjects.get(g);
                                        if (!Util.overlappingSpans(geoPlaceObject.getSpans(), kafEvent.getParticipants())) {
                                            String participantId = "";
                                            if (!format.equalsIgnoreCase("nafrdf")) {
                                                participantId = "rl:"+key+":"+geoPlaceObject.getpId();
                                            }
                                            else {
                                                participantId  = "&docId;"+kafResult.getSentenceId()+":"+tupleElement.getMention();

                                            }
                                            KafParticipant kafParticipant = new KafParticipant();
                                            kafParticipant.setId(participantId);
                                            kafParticipant.setRole("l1");
                                            if (geoPlaceObject.getExternalReferences().size()>0) {
                                                kafParticipant.setExternalReferences(geoPlaceObject.getExternalReferences());
                                            }
                                            kafParticipant.setElementName("place");
                                            kafParticipant.setSpans(geoPlaceObject.getSpans());
                                            kafEvent.addParticipant(kafParticipant);

                                        }
                                    }
                                }
                                if (tupleElement.isoDates.size()>0) {
                                    for (int d = 0; d < tupleElement.isoDates.size(); d++) {
                                        ISODate isoDate = tupleElement.isoDates.get(d);
                                        if (!Util.overlappingSpans(isoDate.getSpans(), kafEvent.getParticipants())) {
                                            String participantId = "";
                                            if (!format.equalsIgnoreCase("nafrdf")) {
                                                participantId = "rl:"+key+":"+isoDate.getDid();
                                            }
                                            else {
                                                participantId = "&docId;"+isoDate.getDid();
                                            }
                                            KafParticipant kafParticipant = new KafParticipant();
                                            if (!isoDate.getDateInfo().getDateISO().isEmpty()) {
                                                KafSense kafSense = new KafSense();
                                                kafSense.setSensecode(isoDate.getDateInfo().getDateISO());
                                                kafParticipant.addExternalReferences(kafSense);
                                            }
                                            kafParticipant.setId(participantId);
                                            kafParticipant.setRole("t1");
                                            kafParticipant.setElementName("time");
                                            kafParticipant.setSpans(isoDate.getSpans());
                                            kafEvent.addParticipant(kafParticipant);
                                        }
                                    }
                                }

                            }
                            for (int p = 0; p < kafResult.getChildren().size(); p++) {
                                if (p!=j) {
                                    TupleElement oTupleElement = kafResult.getChildren().get(p);
                                    if (!oTupleElement.getName().toLowerCase().startsWith("event")) {
                                        String participantId = "";
                                        if (!format.equalsIgnoreCase("nafrdf")) {
                                            participantId = "rl:"+key+":"+kafResult.getSentenceId()+":"+oTupleElement.getMention();
                                        }
                                        else {
                                            participantId = "&docId;"+kafResult.getSentenceId()+":"+oTupleElement.getMention();
                                        }
                                        KafParticipant kafParticipant = new KafParticipant();
                                        kafParticipant.setId(participantId);
                                        kafParticipant.setRole(oTupleElement.getRole());
                                        kafParticipant.setSynsetId(oTupleElement.getConcept());

                                        kafParticipant.setComponentType("participant");
                                        if (!oTupleElement.getName().isEmpty())  {
                                            KafSense kafSense = new KafSense();
                                            String sense = oTupleElement.getName();
                                            int idx = sense.indexOf(":");
                                            if (idx >-1) {
                                                sense = sense.substring(idx);
                                            }
                                            kafSense.setSensecode(sense);
                                            kafSense.setRefType(oTupleElement.getReference());
                                            kafSense.setResource(oTupleElement.getProfileId());
                                            kafParticipant.addExternalReferences(kafSense);
                                        }
                                        if (!oTupleElement.getReference().isEmpty())  {
                                            KafSense kafSense = new KafSense();
                                            String sense = oTupleElement.getReference();
                                            String resource = "";
                                            int idx = sense.indexOf(":");
                                            if (idx >-1) {
                                                resource = sense.substring(0, idx-1);
                                                sense = sense.substring(idx+1);
                                            }
                                            kafSense.setSensecode(sense);
                                            kafSense.setResource(resource);
                                            kafSense.setRefType(oTupleElement.getReference());
                                            kafParticipant.addExternalReferences(kafSense);
                                        }
                                        if (!oTupleElement.getConcept().isEmpty()) {
                                            KafSense kafSense = new KafSense();
                                            kafSense.setSensecode(tupleElement.getConcept());
                                            kafSense.setConfidence(tupleElement.getConfidence());
                                            kafSense.setResource("wn:"+tupleElement.getConcept().substring(0,6));
                                            kafParticipant.addExternalReferences(kafSense);
                                        }
                                        kafParticipant.setElementName(oTupleElement.getName());
                                        kafParticipant.setSynsetConfidence(oTupleElement.getConfidence());
                                        kafParticipant.addSpan(oTupleElement.getMention());
                                        kafEvent.addParticipant(kafParticipant);
                                    }
                                }
                            }
                            kafEventMap.put(eventId, kafEvent);
                        }
                    }
                }
                Set eventSet = kafEventMap.keySet();
                Iterator eventKeys = eventSet.iterator();
                while (eventKeys.hasNext()) {
                    String eventKey = (String) eventKeys.next();
                    KafEvent kafEvent = kafEventMap.get(eventKey);
                    kafSaxParser.kafEventArrayList.add(kafEvent);

                }
            }
        }
    }

}