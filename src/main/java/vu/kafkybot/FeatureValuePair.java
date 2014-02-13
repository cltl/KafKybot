package vu.kafkybot;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/3/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeatureValuePair {
    private String feature;
    private String value;
    private String parent_reference;
    private double parent_score;

    public FeatureValuePair() {
        this.feature = "";
        this.value = "";
        this.parent_reference = "";
        this.parent_score = 0;
    }

    public FeatureValuePair(String feature, String value, String parent_reference, double confidence) {
        this.feature = feature;
        this.value = value;
        this.parent_reference = parent_reference;
        this.parent_score = confidence;
    }

    public FeatureValuePair(String feature, String value) {
        this.feature = feature;
        this.value = value;
        this.parent_reference = "";
        this.parent_score = 0;
    }

    public double getParent_score() {
        return parent_score;
    }

    public void setParent_score(double parent_score) {
        this.parent_score = parent_score;
    }

    public String getParent_reference() {
        return parent_reference;
    }

    public void setParent_reference(String parent_reference) {
        this.parent_reference = parent_reference;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean matchTargetFeatureValue (FeatureValuePair profilePair) {
        if (this.getFeature().equals(profilePair.getFeature())) {
            return  (Util.matchStringValue(this.getValue(), profilePair.getValue()));
        }
        else {
            return false;
            //  System.out.println("this.feature = " + this.getFeature());
            //  System.out.println("pair.feature = " + pair.getFeature());
        }
    }

}
