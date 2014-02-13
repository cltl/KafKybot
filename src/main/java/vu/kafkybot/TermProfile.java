package vu.kafkybot;

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
    private String depRelation;
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
        this.depRelation = "";
        optional = false;
        negative = false;
    }

    public String getDepRelation() {
        return depRelation;
    }

    public void setDepRelation(String depRelation) {
        this.depRelation = depRelation;
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

    public String toString () {
        String str = "";
        for (int i = 0; i < termLayerProperties.size(); i++) {
            FeatureValuePair featureValuePair = termLayerProperties.get(i);
            str += featureValuePair.getFeature()+"="+featureValuePair.getValue()+";";
        }
        return str;
    }
}
