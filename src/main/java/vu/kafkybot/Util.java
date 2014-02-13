package vu.kafkybot;

import eu.kyotoproject.kaf.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/3/13
 * Time: 9:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    public static boolean overlappingSpans (ArrayList<String> spans, ArrayList<KafParticipant> participantArrayList) {
        for (int i = 0; i < participantArrayList.size(); i++) {
            KafParticipant kafParticipant = participantArrayList.get(i);
            for (int j = 0; j < kafParticipant.getSpans().size(); j++) {
                String s = kafParticipant.getSpanIds().get(j);
                if (spans.contains(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasMatchingProperties(KafSaxParser kafSaxParser, KafTerm term, TermProfile termProfile) {
        boolean  match = hasMatchingTermPropertiesValue(term, termProfile);
        if (match) {
            if (termProfile.getTermChunkProperties().size()>0) {
                boolean chunkMatch = false;
                ArrayList<String> chunkIds = kafSaxParser.TermToChunk.get(term.getTid());
                if (chunkIds!=null) {
                    for (int i = 0; i < chunkIds.size(); i++) {
                        String id = chunkIds.get(i);
                        KafChunk kafChunk = kafSaxParser.getChunks(id);
                        if (hasMatchingTermChunksValue(kafChunk, term.getTid(), termProfile)) {
                            chunkMatch = true;
                            break;
                        }
                    }
                }
                else {
                    //  System.out.println("No chunks for termId = " + termId);
                }

                if (!chunkMatch) {
                    match = false;
                }
            }
        }
        if (match) {
            if (termProfile.getTermTokenProperties().size()>0) {
                boolean tokenMatch = false;
                ArrayList<String> tokenIds = kafSaxParser.TermToWord.get(term.getTid());
                for (int i = 0; i < tokenIds.size(); i++) {
                    String id = tokenIds.get(i);
                    KafWordForm kafWordForm = kafSaxParser.getWordForm(id);
                    if (hasMatchingTermTokensValue(kafWordForm, termProfile)) {
                        tokenMatch = true;
                        break;
                    }

                }
                if (!tokenMatch) {
                    match = false;
                }
            }
        }
        if (match) {
            if (termProfile.getTermDependenciesProperties().size()>0) {
                boolean depMatch = false;
                ArrayList<KafDep> deps = kafSaxParser.TermToDeps.get(term.getTid());
                if (deps!=null) {
                    for (int i = 0; i < deps.size(); i++) {
                        KafDep kafDep = deps.get(i);
                        if (hasMatchingDependencyPropertiesValue(kafDep, termProfile)) {
                            depMatch = true;
                            break;
                        }

                    }
                }
                if (!depMatch) {
                    match = false;
                }
            }
        }
        return match;
    }

    public static boolean hasMatchingTermPropertiesValue(KafTerm term, TermProfile termProfile) {
        ArrayList<FeatureValuePair> termFeatureValuePairs = Util.kafTermToFeatureValue(term);
        for (int i = 0; i < termProfile.getTermLayerProperties().size(); i++) {
            FeatureValuePair profileFeatureValuePair = termProfile.getTermLayerProperties().get(i);
            boolean match = false;
            if (profileFeatureValuePair.getValue().startsWith("!")) {
                /// negative case
                match = true;
            }

            for (int j = 0; j < termFeatureValuePairs.size(); j++) {
                FeatureValuePair pair = termFeatureValuePairs.get(j);
                if (!pair.getValue().isEmpty())  {
                    if (pair.matchTargetFeatureValue(profileFeatureValuePair)) {
                        if (profileFeatureValuePair.getValue().startsWith("!")) {
                            /// immediately quit!
                            termProfile.setNegative(true);
                            match = false;
                            break;
                        }
                        else {
                            termProfile.setNegative(false);
                        }
                        if (!pair.getParent_reference().isEmpty()) {
                            termProfile.setParent_confidence(pair.getParent_score());
                            termProfile.setParent_reference(pair.getParent_reference());
                        }
                        if (pair.getFeature().equalsIgnoreCase("reference")) {
                            termProfile.setReference(pair.getValue());
                        }
                        match = true;
                        break;
                    }
                    /*else if (profileFeatureValuePair.getValue().startsWith("!")) {
                        /// immediately quit!
                        if (!pair.getParent_reference().isEmpty()) {
                            termProfile.setParent_confidence(pair.getParent_score());
                            termProfile.setParent_reference(pair.getParent_reference());
                        }
                        if (pair.getFeature().equalsIgnoreCase("reference")) {
                            termProfile.setReference(pair.getValue());
                        }
                        termProfile.setNegative(true);
                        match = true;
                        break;
                    }*/
                }
            }
            if (!match) {
                /// the first condition that is not matched results in a fail
                return false;
            }
        }
        /// all conditions have been met so success
        return true;
    }

    public static boolean hasMatchingDependencyPropertiesValue(KafDep kafDep, TermProfile termProfile) {
        ArrayList<FeatureValuePair> termFeatureValuePairs = Util.kafDepToFeatureValue(kafDep);
        for (int i = 0; i < termProfile.getTermDependenciesProperties().size(); i++) {
            FeatureValuePair profileFeatureValuePair = termProfile.getTermDependenciesProperties().get(i);

            //// WE SKIP to AND from since they are used to relate one variable to another and not as a feature
            if (profileFeatureValuePair.getFeature().equals("to")) {
                continue;
            }
            if (profileFeatureValuePair.getFeature().equals("from")) {
                continue;
            }
/*
            System.out.println("profileFeatureValuePair.getValue() = " + profileFeatureValuePair.getValue());
            System.out.println("profileFeatureValuePair.getFeature() = " + profileFeatureValuePair.getFeature());
*/
            boolean match = false;
            if (profileFeatureValuePair.getValue().startsWith("!")) {
                /// negative case
                match = true;
            }

            for (int j = 0; j < termFeatureValuePairs.size(); j++) {
                FeatureValuePair pair = termFeatureValuePairs.get(j);
                if (!pair.getValue().isEmpty())  {
                    if (pair.matchTargetFeatureValue(profileFeatureValuePair)) {
                        if (profileFeatureValuePair.getValue().startsWith("!")) {
                            /// immediately quit!
                            termProfile.setNegative(true);
                            match = false;
                            break;
                        }
                        else {
                            termProfile.setNegative(false);
                        }
                        if (!pair.getFeature().isEmpty()) {
                            termProfile.setDepRelation(pair.getValue());
                        }
                        match = true;
                        break;
                    }
                }
            }
            if (!match) {
                /// the first condition that is not matched results in a fail
                return false;
            }
        }
        /// all conditions have been met so success
        return true;
    }


    public static boolean hasMatchingTermChunksValue(KafChunk chunk, String termId, TermProfile termProfile) {
        ArrayList<FeatureValuePair> termFeatureValuePairs = Util.kafChunkToFeatureValue(chunk);
        for (int i = 0; i < termProfile.getTermChunkProperties().size(); i++) {
            FeatureValuePair profileFeatureValuePair = termProfile.getTermChunkProperties().get(i);
            boolean match = false;
            if (profileFeatureValuePair.getValue().startsWith("!")) {
                /// negative case, os if nothing matches the result is true
                match = true;
            }

            for (int j = 0; j < termFeatureValuePairs.size(); j++) {
                FeatureValuePair pair = termFeatureValuePairs.get(j);
                if (profileFeatureValuePair.getFeature().equals(pair.getFeature()))   {
                    if (profileFeatureValuePair.getFeature().equals("head")) {
                        if (chunk.getHead().equals(termId)) {
                            if (profileFeatureValuePair.getValue().startsWith("!")) {
                                /// immediately quit!
                                termProfile.setNegative(true);
                                match = false;
                                break;
                            }
                            else {
                                termProfile.setNegative(false);
                                match = true;
                                break;
                            }
                        }
                    }
                    else if (!pair.getValue().isEmpty())  {
                        if (pair.matchTargetFeatureValue(profileFeatureValuePair)) {
                            if (profileFeatureValuePair.getValue().startsWith("!")) {
                                /// immediately quit!
                                termProfile.setNegative(true);
                                match = false;
                                break;
                            }
                            else {
                                termProfile.setNegative(false);
                                match = true;
                                break;
                            }
                        }
                    }
                    else {

                    }
                }
            }
            if (!match) {
                /// the first condition that is not matched results in a fail
                return false;
            }
        }
        /// all conditions have been met so success
        return true;
    }

    public static boolean hasMatchingTermTokensValue(KafWordForm wordForm, TermProfile termProfile) {
        ArrayList<FeatureValuePair> termFeatureValuePairs = Util.kafWordFormToFeatureValue(wordForm);
        for (int i = 0; i < termProfile.getTermTokenProperties().size(); i++) {
            FeatureValuePair profileFeatureValuePair = termProfile.getTermTokenProperties().get(i);
            /// for the new version that uses real dependencies, we skip these constraints
            // if (term.getLemma().equals("people")) System.out.println("profileFeatureValuePair.getValue() = " + profileFeatureValuePair.getValue());
            boolean match = false;
            if (profileFeatureValuePair.getValue().startsWith("!")) {
                /// negative case
                match = true;
            }

            for (int j = 0; j < termFeatureValuePairs.size(); j++) {
                FeatureValuePair pair = termFeatureValuePairs.get(j);
                if (!pair.getValue().isEmpty())  {
                    if (pair.matchTargetFeatureValue(profileFeatureValuePair)) {
                        if (profileFeatureValuePair.getValue().startsWith("!")) {
                            /// immediately quit!
                            termProfile.setNegative(true);
                            match = false;
                            break;
                        }
                        else {
                            termProfile.setNegative(false);
                        }
                        match = true;
                        break;
                    }
                }
            }
            if (!match) {
                /// the first condition that is not matched results in a fail
                return false;
            }
        }
        /// all conditions have been met so success
        return true;
    }


    static public ArrayList<FeatureValuePair> kafSenseToFeatureValue (KafSense kafSense, String parent, double conf) {
        /*
              KafSense
      private String resource;
private String sensecode;
private double confidence;
private String refType = "", status = "";
        */
        ArrayList<FeatureValuePair> arrayListFeatureValuePairs = new ArrayList<FeatureValuePair>();
        FeatureValuePair pair = new FeatureValuePair("reftype", kafSense.getRefType(), parent, conf);
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("reference", kafSense.getSensecode(), parent, conf);
        arrayListFeatureValuePairs.add(pair);
/*        pair = new vu.kafkybot.FeatureValuePair("concept", parent, parent, conf);
        arrayListFeatureValuePairs.add(pair);*/
        pair = new FeatureValuePair("status", kafSense.getStatus(), parent, conf);
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("resource", kafSense.getResource(), parent, conf);
        arrayListFeatureValuePairs.add(pair);
        for (int j = 0; j < kafSense.getChildren().size(); j++) {
            KafSense childSense = kafSense.getChildren().get(j);
            ArrayList<FeatureValuePair> childValues = kafSenseToFeatureValue(childSense, parent, conf);
            for (int i = 0; i < childValues.size(); i++) {
                FeatureValuePair featureValuePair = childValues.get(i);
                arrayListFeatureValuePairs.add(featureValuePair);
            }
        }
        return arrayListFeatureValuePairs;
    }


    static public ArrayList<FeatureValuePair> kafChunkToFeatureValue (KafChunk chunk) {
        /*
    <chunk cid="c1" head="t2" phrase="NP">
      <span>
        <target tid="t1"/>
        <target tid="t2"/>
      </span>
    </chunk>
        */

        ArrayList<FeatureValuePair> arrayListFeatureValuePairs = new ArrayList<FeatureValuePair>();
        FeatureValuePair pair = new FeatureValuePair("phrase", chunk.getPhrase());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("head", chunk.getHead());
        arrayListFeatureValuePairs.add(pair);
        return arrayListFeatureValuePairs;
    }

    static public ArrayList<FeatureValuePair> kafDepToFeatureValue (KafDep dep) {
        /*
 <dep from="t193" rfunc="ccomp" to="t195"/>
<dep from="t187" rfunc="conj" to="t193"/>
       */

        ArrayList<FeatureValuePair> arrayListFeatureValuePairs = new ArrayList<FeatureValuePair>();
        FeatureValuePair pair = new FeatureValuePair("rfunc", dep.getRfunc());
        arrayListFeatureValuePairs.add(pair);
/*
        pair = new FeatureValuePair("to", dep.getTo());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("from", dep.getFrom());
        arrayListFeatureValuePairs.add(pair);
*/
        return arrayListFeatureValuePairs;
    }



    static public ArrayList<FeatureValuePair> kafWordFormToFeatureValue (KafWordForm wordForm) {
        /*
   <wf sent="s1" wid="w1">The</wf>
    <wf sent="s1" wid="w2">Queen</wf>
    <wf sent="s1" wid="w3">made</wf>
    <wf sent="s1" wid="w4">the</wf>
    <wf sent="s1" wid="w5">short</wf>
    <wf sent="s1" wid="w6">journey</wf>
        */

        ArrayList<FeatureValuePair> arrayListFeatureValuePairs = new ArrayList<FeatureValuePair>();
        FeatureValuePair pair = new FeatureValuePair("sentence", wordForm.getSent());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("page", wordForm.getPage());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("para", wordForm.getPara());
        arrayListFeatureValuePairs.add(pair);
        return arrayListFeatureValuePairs;
    }

    static public ArrayList<FeatureValuePair> kafTermToFeatureValue (KafTerm term) {
        /*
               this.dep = "";
       this.lemma = "";
       this.morphofeat = "";
       this.pos = "";
       this.type = "";
       this.head ="";
       this.parent ="";
       this.modifier = "";
       this.polarity = "";
       netype ="";
       nFreq = 0;
       this.kafTermSentiment = new KafTermSentiment();
       this.spans = new ArrayList<String>();
       this.tokenString = "";
       this.senseTags = new ArrayList<KafSense>();
       this.components = new ArrayList<TermComponent>();
        */

        ArrayList<FeatureValuePair> arrayListFeatureValuePairs = new ArrayList<FeatureValuePair>();
        FeatureValuePair pair = new FeatureValuePair("dep", term.getDep());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("lemma", term.getLemma());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("morphofeat", term.getMorphofeat());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("pos", term.getPos());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("type", term.getType());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("head", term.getHead());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("parent", term.getParent());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("modifier", term.getModifier());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("netype", term.getNetype());
        arrayListFeatureValuePairs.add(pair);
        for (int i = 0; i < term.getSenseTags().size(); i++) {
            KafSense kafSense = term.getSenseTags().get(i);
            ArrayList<FeatureValuePair> featureValuePairs = kafSenseToFeatureValue (kafSense, kafSense.getSensecode(), kafSense.getConfidence());
            for (int j = 0; j < featureValuePairs.size(); j++) {
                FeatureValuePair featureValuePair = featureValuePairs.get(j);
                arrayListFeatureValuePairs.add(featureValuePair);
            }
        }
        KafTermSentiment kafTermSentiment = term.getKafTermSentiment();
        if (kafTermSentiment!= null) {
            pair = new FeatureValuePair("polarity", kafTermSentiment.getPolarity());
            arrayListFeatureValuePairs.add(pair);
            pair = new FeatureValuePair("strength", kafTermSentiment.getStrength());
            arrayListFeatureValuePairs.add(pair);
            pair = new FeatureValuePair("sentiment_modifier", kafTermSentiment.getSentiment_modifier());
            arrayListFeatureValuePairs.add(pair);
        }
        /*
                KafSentiment

            private String resource;
    private String polarity;
    private String strength;
    private String subjectivity;
    private String sentiment_modifier;
    private String factual;
    private String sentiment_semantic_type;
    private String sentiment_product_feature;
         */
        pair = new FeatureValuePair("lemma", term.getLemma());
        arrayListFeatureValuePairs.add(pair);
/*        for (int i = 0; i < arrayListFeatureValuePairs.size(); i++) {
            vu.kafkybot.FeatureValuePair featureValuePair = arrayListFeatureValuePairs.get(i);
            System.out.println("featureValuePair.getFeature() = " + featureValuePair.getFeature());
            System.out.println("featureValuePair.getValue() = " + featureValuePair.getValue());
        }*/
        return arrayListFeatureValuePairs;
    }

    static public boolean matchStringValue (String targetValue, String profileValue) {

        /// strip off negation marker
        if (profileValue.startsWith("!")) {
            profileValue = profileValue.substring(1);
        }

        String [] profileValues = profileValue.split(" OR ");
        for (int i = 0; i < profileValues.length; i++) {
            String pValue = profileValues[i].trim();
            //System.out.println("pValue after OR = " + pValue);
            String matchType = "";
            if (pValue.equals("*")) {
                return true;
            }
            if ((pValue.endsWith("*")) && (pValue.startsWith("*"))) {
                matchType = "sub";
                pValue = pValue.substring(1, pValue.length() - 1);
            }
            else if (pValue.endsWith("*")) {
                matchType = "start";
                pValue = pValue.substring(0, pValue.length()-1);
            }
            else if (pValue.startsWith("*")) {
                matchType = "end";
                pValue = pValue.substring(1, pValue.length());
            }
           // System.out.println("true pValue = " + pValue);
            if (matchType.equals("sub")) {
                if (targetValue.indexOf(pValue)>-1) {
/*
                System.out.println("SUB");
                System.out.println("targetValue = " + targetValue);
                System.out.println("pValue = " + pValue);
*/
                    return true;
                }
            }
            else if (matchType.equals("start")) {
                if (targetValue.startsWith(pValue)) {
/*
                System.out.println("START");
                System.out.println("targetValue = " + targetValue);
                System.out.println("pValue = " + pValue);
*/
                    return true;
                }
            }
            else if (matchType.equals("end")) {
                if (targetValue.endsWith(profileValue)) {
/*
                System.out.println("END");
                System.out.println("targetValue = " + targetValue);
                System.out.println("pValue = " + pValue);
*/
                    return true;
                }
            }
            //else if (this.getValue().equals(value)) {
            else if (targetValue.equalsIgnoreCase(pValue)) {
/*
                System.out.println("EXACT");
                System.out.println("targetValue = " + targetValue);
                System.out.println("pValue = " + pValue);
*/
                return true;
            }
        }
        return false;
    }

    static public ArrayList<String> makeRecursiveFileListAll(String inputPath, String extension) {
        ArrayList<String> acceptedFileList = new ArrayList<String>();
        ArrayList<String>  nestedFileList = new ArrayList<String>();
        File[] theFileList = null;
        File lF = new File(inputPath);
        if ((lF.canRead()) && lF.isDirectory()) {
            theFileList = lF.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                String newFilePath = theFileList[i].getAbsolutePath();
                //   System.out.println("newFilePath = " + newFilePath);
                if (theFileList[i].isDirectory()) {
                    nestedFileList = makeRecursiveFileListAll(newFilePath, extension);
                    for (int j = 0; j < nestedFileList.size(); j++) {
                        String s = nestedFileList.get(j);
                        if (s.endsWith(extension)) {
                            acceptedFileList.add(s);
                        }
                    }
                } else {
                    if (newFilePath.endsWith(extension)) {
                        acceptedFileList.add(newFilePath);
                    }
                }
            }
        }
        return acceptedFileList;
    }

    static public boolean hasTuple (ArrayList<KafResult> results, KafResult kafResult) {
        for (int i = 0; i < results.size(); i++) {
            KafResult result = results.get(i);
            if (result.isEqual(kafResult)) {
                return true;
            }
        }
        return false;
    }
}
