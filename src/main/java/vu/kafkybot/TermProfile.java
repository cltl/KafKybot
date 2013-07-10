package vu.kafkybot;

import eu.kyotoproject.kaf.*;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/3/13
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class TermProfile {

    private ArrayList<FeatureValuePair> termLayerProperties;
    private ArrayList<FeatureValuePair> termDependenciesProperties;
    private ArrayList<FeatureValuePair> termChunkProperties;
    private ArrayList<FeatureValuePair> termTokenProperties;
    private String type;
    private String role;
    private boolean optional;
    private boolean negative;
    private int next;
    private String parent_reference;
    private double parent_confidence;
    private String reference;
    private String variableId;

    public TermProfile() {
       init();
    }

    void init () {
        this.variableId = "";
        this.termLayerProperties = new ArrayList<FeatureValuePair>();
        this.termDependenciesProperties = new ArrayList<FeatureValuePair>();
        this.termChunkProperties = new ArrayList<FeatureValuePair>();
        this.termTokenProperties = new ArrayList<FeatureValuePair>();
        this.type = "";
        this.role = "";
        this.next = 0;
        this.reference = "";
        this.parent_reference = "";
        this.parent_confidence = 0;
        optional = false;
        negative = false;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public double getParent_confidence() {
        return parent_confidence;
    }

    public void setParent_confidence(double parent_confidence) {
        this.parent_confidence = parent_confidence;
    }

    public String getParent_reference() {
        return parent_reference;
    }

    public void setParent_reference(String parent_reference) {
        this.parent_reference = parent_reference;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void addFeatureValuePairToTermProperties(FeatureValuePair pair) {
         this.termLayerProperties.add(pair);
    }
    public void addFeatureValuePairToTermDependencies(FeatureValuePair pair) {
         this.termDependenciesProperties.add(pair);
    }
    public void addFeatureValuePairToTermChunks(FeatureValuePair pair) {
         this.termChunkProperties.add(pair);
    }
    public void addFeatureValuePairToTermTokens(FeatureValuePair pair) {
         this.termTokenProperties.add(pair);
    }

    public ArrayList<FeatureValuePair> getTermLayerProperties() {
        return termLayerProperties;
    }

    public ArrayList<FeatureValuePair> getTermChunkProperties() {
        return termChunkProperties;
    }

    public ArrayList<FeatureValuePair> getTermDependenciesProperties() {
        return termDependenciesProperties;
    }

    public ArrayList<FeatureValuePair> getTermTokenProperties() {
        return termTokenProperties;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean hasMatchingProperties (KafSaxParser kafSaxParser, KafTerm term) {
        boolean  match = hasMatchingTermPropertiesValue(term);
        if (match) {
            if (this.getTermChunkProperties().size()>0) {
                boolean chunkMatch = false;
                ArrayList<String> chunkIds = kafSaxParser.TermToChunk.get(term.getTid());
                if (chunkIds!=null) {
                    for (int i = 0; i < chunkIds.size(); i++) {
                        String id = chunkIds.get(i);
                        KafChunk kafChunk = kafSaxParser.getChunks(id);
                        if (this.hasMatchingTermChunksValue(kafChunk, term.getTid())) {
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
            if (this.getTermTokenProperties().size()>0) {
                boolean tokenMatch = false;
                ArrayList<String> tokenIds = kafSaxParser.TermToWord.get(term.getTid());
                for (int i = 0; i < tokenIds.size(); i++) {
                    String id = tokenIds.get(i);
                    KafWordForm kafWordForm = kafSaxParser.getWordForm(id);
                    if (this.hasMatchingTermTokensValue(kafWordForm)) {
                        tokenMatch = true;
                        break;
                    }

                }
                if (!tokenMatch) {
                    match = false;
                }
            }
        }
        return match;
    }

    public boolean hasMatchingTermPropertiesValue(KafTerm term) {
        ArrayList<FeatureValuePair> termFeatureValuePairs = kafTermToFeatureValue(term);
        for (int i = 0; i < termLayerProperties.size(); i++) {
            FeatureValuePair profileFeatureValuePair = termLayerProperties.get(i);
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
                    if (pair.matchFeatureValue(profileFeatureValuePair)) {
                        if (profileFeatureValuePair.getValue().startsWith("!")) {
                            /// immediately quit!
                            this.setNegative(true);
                            match = false;
                            break;
                        }
                        else {
                            this.setNegative(false);
                        }
                        if (!pair.getParent_reference().isEmpty()) {
                            this.parent_confidence = pair.getParent_score();
                            this.parent_reference = pair.getParent_reference();
                        }
                        if (pair.getFeature().equalsIgnoreCase("reference")) {
                            this.reference = pair.getValue();
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


    public boolean hasMatchingTermChunksValue(KafChunk chunk, String termId) {
        ArrayList<FeatureValuePair> termFeatureValuePairs = kafChunkToFeatureValue(chunk);
        for (int i = 0; i < termChunkProperties.size(); i++) {
            FeatureValuePair profileFeatureValuePair = termChunkProperties.get(i);
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
                                this.setNegative(true);
                                match = false;
                                break;
                            }
                            else {
                                this.setNegative(false);
                                match = true;
                                break;
                            }
                        }
                    }
                    else if (!pair.getValue().isEmpty())  {
                        if (pair.matchFeatureValue(profileFeatureValuePair)) {
                            if (profileFeatureValuePair.getValue().startsWith("!")) {
                                /// immediately quit!
                                this.setNegative(true);
                                match = false;
                                break;
                            }
                            else {
                                this.setNegative(false);
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

    public boolean hasMatchingTermTokensValue(KafWordForm wordForm) {
        ArrayList<FeatureValuePair> termFeatureValuePairs = kafWordFormToFeatureValue(wordForm);
        for (int i = 0; i < termTokenProperties.size(); i++) {
            FeatureValuePair profileFeatureValuePair = termTokenProperties.get(i);
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
                    if (pair.matchFeatureValue(profileFeatureValuePair)) {
                        if (profileFeatureValuePair.getValue().startsWith("!")) {
                            /// immediately quit!
                            this.setNegative(true);
                            match = false;
                            break;
                        }
                        else {
                            this.setNegative(false);
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


    public ArrayList<FeatureValuePair> kafSenseToFeatureValue (KafSense kafSense, String parent, double conf) {
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


    public ArrayList<FeatureValuePair> kafChunkToFeatureValue (KafChunk chunk) {
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


    public ArrayList<FeatureValuePair> kafDependenciesToFeatureValue (KafDep dep) {
        /*
    <dep from="t151" rfunc="pobj" to="t152"/>
    <dep from="t148" rfunc="prep" to="t151"/>
    <dep from="t155" rfunc="nn" to="t154"/>
    <dep from="t153" rfunc="pobj" to="t155"/>
    <dep from="t148" rfunc="prep" to="t153"/>
    <dep from="root-0" rfunc="root" to="t148"/>
        */

        ArrayList<FeatureValuePair> arrayListFeatureValuePairs = new ArrayList<FeatureValuePair>();
        FeatureValuePair pair = new FeatureValuePair("rfunc", dep.getRfunc());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("from", dep.getFrom());
        arrayListFeatureValuePairs.add(pair);
        pair = new FeatureValuePair("to", dep.getTo());
        arrayListFeatureValuePairs.add(pair);
        return arrayListFeatureValuePairs;
    }

    public ArrayList<FeatureValuePair> kafWordFormToFeatureValue (KafWordForm wordForm) {
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

    public ArrayList<FeatureValuePair> kafTermToFeatureValue (KafTerm term) {
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

    public String toString () {
        String str = "";
        for (int i = 0; i < termLayerProperties.size(); i++) {
            FeatureValuePair featureValuePair = termLayerProperties.get(i);
            str += featureValuePair.getFeature()+"="+featureValuePair.getValue()+";";
        }
        return str;
    }
}
