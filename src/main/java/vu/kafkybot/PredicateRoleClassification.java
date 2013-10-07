package vu.kafkybot;

import eu.kyotoproject.kaf.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 10/6/13
 * Time: 8:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PredicateRoleClassification {

    static final String layer = "predicate";
    static final String name = "vua-predicate-role-classification";
    static final String version = "1.0";

    static public void main (String[] args) {
        String pathToKafFile = "/Tools/kafkybot.v.0.1/naf-example/world_us_canada_new.pm.naf";
        String method = "wsd";   //
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equals("--kaf-file")) && args.length>i+1) {
                pathToKafFile = args[i+1];
            }
            if ((arg.equals("--method")) && args.length>i+1) {
                method = args[i+1];
            }
        }
        File kafFile = new File(pathToKafFile);
        KafSaxParser kafSaxParser = new KafSaxParser();
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
        processKafFile(kafSaxParser);
        kafSaxParser.writeNafToStream(System.out);
    }

    static void processKafFile(KafSaxParser kafSaxParser) {
        for (int i = 0; i < kafSaxParser.getKafEventArrayList().size(); i++) {
            KafEvent kafEvent = kafSaxParser.getKafEventArrayList().get(i);
            processPredicateByWsdScore(kafEvent, kafSaxParser);
            for (int j = 0; j < kafEvent.getParticipants().size(); j++) {
                KafParticipant kafParticipant = kafEvent.getParticipants().get(j);
                processRolesByWsdScore(kafParticipant, kafSaxParser);
            }
        }
    }

    static void processPredicateByWsdScore(KafEvent kafEvent, KafSaxParser kafSaxParser) {
        ArrayList<String> spans = kafEvent.getSpans();
        double topscore = 0;
        KafSense bestSense = null;
        String termId = "";
        for (int j = 0; j < spans.size(); j++) {
            String s = spans.get(j);
            KafTerm kafTerm = kafSaxParser.getTerm(s);
            if (kafTerm!=null) {
                boolean grammatical = false;
                for (int i = 0; i < kafTerm.getSenseTags().size(); i++) {
                    KafSense kafSense = kafTerm.getSenseTags().get(i);
                    if (kafSense.getSensecode().equalsIgnoreCase("grammatical")) {
                        grammatical = true;
                        String sentenceId = kafSaxParser.getSentenceId(kafTerm);
                        kafEvent.setSentenceId(sentenceId);
                        kafEvent.addExternalReferences(kafSense);
                        break;
                    }
                }
                if (!grammatical) {
                    for (int i = 0; i < kafTerm.getSenseTags().size(); i++) {
                        KafSense kafSense = kafTerm.getSenseTags().get(i);
                        for (int k = 0; k < kafSense.getChildren().size(); k++) {
                            KafSense sense = kafSense.getChildren().get(k);
                            if ((sense.getSensecode().startsWith("mcr:"))
                            || (sense.getSensecode().startsWith("vn:"))
                            || (sense.getSensecode().startsWith("fn:"))
                            || (sense.getSensecode().startsWith("pb:"))) {
                                if (kafSense.getConfidence()>topscore) {
                                    bestSense = kafSense;
                                    topscore = kafSense.getConfidence();
                                    termId = kafTerm.getTid();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (bestSense!=null) {
            String sentenceId = kafSaxParser.getSentenceId(termId);
            kafEvent.setSentenceId(sentenceId);
            kafEvent.addExternalReferences(bestSense);
        }
    }

    static void processRolesByWsdScore(KafParticipant kafParticipant, KafSaxParser kafSaxParser) {
        ArrayList<String> spans = kafParticipant.getSpans();
        double topscore = 0;
        KafSense bestSense = null;
        String termId = "";
        for (int j = 0; j < spans.size(); j++) {
            String s = spans.get(j);
            KafTerm kafTerm = kafSaxParser.getTerm(s);
            if (kafTerm!=null) {
                for (int i = 0; i < kafTerm.getSenseTags().size(); i++) {
                    KafSense kafSense = kafTerm.getSenseTags().get(i);
                    if (kafSense.getConfidence()>topscore) {
                       bestSense = kafSense;
                       topscore = kafSense.getConfidence();
                       termId = kafTerm.getTid();
                    }
                }
            }
        }
        if (bestSense!=null) {
            String sentenceId = kafSaxParser.getSentenceId(termId);
            kafParticipant.setSentenceId(sentenceId);
            kafParticipant.addExternalReferences(bestSense);
        }
    }
}
