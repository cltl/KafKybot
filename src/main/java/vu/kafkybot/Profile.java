package vu.kafkybot;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/4/13
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Profile {

    private String name;
    private String order;
    private int confidence;
    ArrayList<TermProfile> termProfiles;

    public Profile() {
        this.name = "";
        this.order = "";
        this.confidence = 0;
        this.termProfiles = new ArrayList<TermProfile>();
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public ArrayList<TermProfile> getTermProfiles() {
        return termProfiles;
    }

    public void setTermProfiles(ArrayList<TermProfile> termProfiles) {
        this.termProfiles = termProfiles;
    }

    public void addTermProfiles(TermProfile termProfile) {
        this.termProfiles.add(termProfile);
    }

    public String toString () {
        String str = "name="+name+" order="+ order+ " confidence="+confidence;
        return str;
    }
}
