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

    public boolean matchFeatureValue (FeatureValuePair pair) {
        if (this.getFeature().equals(pair.getFeature())) {
            String [] values = pair.getValue().split(" OR ");
            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                //System.out.println("value = " + value);
                boolean neg = false;
                String matchType = "";
                if (value.equals("*")) {
                    return true;
                }
                /// strip off negation marker
                if (pair.getValue().startsWith("!")) {
                    value = pair.getValue().substring(1);
                    //System.out.println("value = " + value);
                }

                if ((value.endsWith("*")) && (value.startsWith("*"))) {
                    matchType = "sub";
                    value = value.substring(1, value.length() - 1);
                }
                else if (value.endsWith("*")) {
                    matchType = "start";
                    value = value.substring(0, value.length() - 1);
                }
                else if (value.startsWith("*")) {
                    matchType = "end";
                    value = value.substring(1, value.length());
                }
                // System.out.println("value = " + value);
                if (matchType.equals("sub")) {
                    if (this.getValue().indexOf(value)>-1) {
                        /*System.out.println("SUB");
                    System.out.println("this.getValue() = " + this.getValue());
                    System.out.println("pair.getValue() = " + pair.getValue());*/
                        return true;
                    }
                }
                else if (matchType.equals("start")) {
                    if (this.getValue().startsWith(value)) {
                        /*System.out.println("START");
                    System.out.println("this.getValue() = " + this.getValue());
                    System.out.println("pair.getValue() = " + pair.getValue());*/
                        return true;
                    }
                }
                else if (matchType.equals("end")) {
                    if (this.getValue().endsWith(value)) {
                        /*System.out.println("END");
                    System.out.println("this.getValue() = " + this.getValue());
                    System.out.println("pair.getValue() = " + pair.getValue());*/
                        return true;
                    }
                }
                //else if (this.getValue().equals(value)) {
                else if (this.getValue().equalsIgnoreCase(value)) {
                    /*System.out.println("EXACT");
                System.out.println("this.getValue() = " + this.getValue());
                System.out.println("pair.getValue() = " + pair.getValue());*/
                    return true;
                }
            }
        }
        else {
            return false;
            //  System.out.println("this.feature = " + this.getFeature());
            //  System.out.println("pair.feature = " + pair.getFeature());
        }
        return false;
    }

}
