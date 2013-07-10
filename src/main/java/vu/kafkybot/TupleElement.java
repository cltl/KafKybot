package vu.kafkybot;

import eu.kyotoproject.kaf.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/4/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TupleElement {

    private String profileId;
    private String lemma;
    private String name;
    private String concept;
    private String reference;
    private String pos;
    private double confidence;
    private String mention;
    private ArrayList<String> tokens;
    private String role;
    private String depFrom;
    private String depTo;
    private String depRelation;

    ArrayList<GeoCountryObject> geoCountryObjects = new ArrayList<GeoCountryObject>();
    ArrayList<GeoPlaceObject> geoPlaceObjects = new ArrayList<GeoPlaceObject>();
    ArrayList<ISODate> isoDates = new ArrayList<ISODate>();



    public TupleElement() {
       init();
    }

    /**
     *
     * @param name
     * @param kafTerm
     * @param termProfile
     */

    public TupleElement(String name, KafTerm kafTerm, TermProfile termProfile) {
        init();
        this.profileId = termProfile.getVariableId();
        this.name = name;
        if (this.name.isEmpty()) {
            name = "element";
        }

        //// new style that uses real dependencies
        for (int i = 0; i < termProfile.getTermDependenciesProperties().size(); i++) {
            FeatureValuePair featureValuePair = termProfile.getTermDependenciesProperties().get(i);
            if (featureValuePair.getFeature().equals("to")) {
                this.depTo = featureValuePair.getValue().trim();
                break;
            }
        }
        for (int i = 0; i < termProfile.getTermDependenciesProperties().size(); i++) {
            FeatureValuePair featureValuePair = termProfile.getTermDependenciesProperties().get(i);
            if (featureValuePair.getFeature().equals("from")) {
                this.depTo = featureValuePair.getValue().trim();
                break;
            }
        }
        for (int i = 0; i < termProfile.getTermDependenciesProperties().size(); i++) {
            FeatureValuePair featureValuePair = termProfile.getTermDependenciesProperties().get(i);
            if (featureValuePair.getFeature().equals("rfunc")) {
                this.depRelation = featureValuePair.getValue().trim();
                break;
            }
        }

        this.setTokens(kafTerm.getSpans());
        this.setMention(kafTerm.getTid());
        this.setLemma(kafTerm.getLemma());
        this.setConcept(termProfile.getParent_reference());
        this.setConfidence(termProfile.getParent_confidence());
        this.setReference(termProfile.getReference());
        this.setPos(kafTerm.getPos());
        this.setRole(termProfile.getRole());
      //  System.out.println("this.getLemma() = " + this.getLemma());
    }

    void init() {
        this.profileId = "";
        this.name = "";
        this.depFrom = "";
        this.depTo = "";
        this.depRelation = "";
        this.concept = "";
        this.reference = "";
        this.confidence = 0;
        this.lemma = "";
        this.tokens = new ArrayList<String>();
        this.mention = "";
        this.pos = "";
        this.role = "";
        this.geoCountryObjects = new ArrayList<GeoCountryObject>();
        this.geoPlaceObjects = new ArrayList<GeoPlaceObject>();
        this.isoDates = new ArrayList<ISODate>();
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    public void setTokens(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            this.tokens.add(token);
        }
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDepRelation() {
        return depRelation;
    }

    public void setDepRelation(String depRelation) {
        this.depRelation = depRelation;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getDepTo() {
        return depTo;
    }

    public void setDepTo(String depTo) {
        this.depTo = depTo;
    }

    public String getDepFrom() {
        return depFrom;
    }

    public void setDepFrom(String depFrom) {
        this.depFrom = depFrom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getMention() {
        return mention;
    }

    public void setMention(String mention) {
        this.mention = mention;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setTimeAndPlace (KafSaxParser kafSaxParser) {
        this.geoCountryObjects = kafSaxParser.getCountries(this.getMention());
        this.geoPlaceObjects = kafSaxParser.getPlaces(this.getMention());
        this.isoDates = kafSaxParser.getDates(this.getMention());
/*
        if (this.geoCountryObjects.size()>1) {
            int nMentions = 0;
            GeoCountryObject bestGeoCountryObject = null;
            for (int i = 0; i < geoCountryObjects.size(); i++) {
                GeoCountryObject geoCountryObject = geoCountryObjects.get(i);
                if (geoCountryObject.getKafReferences().size()>nMentions) {
                    bestGeoCountryObject = geoCountryObject;
                    nMentions = geoCountryObject.getKafReferences().size();
                }
            }
            if (bestGeoCountryObject!=null) {
                this.geoCountryObjects = new ArrayList<GeoCountryObject>();
                this.geoCountryObjects.add(bestGeoCountryObject);
            }
        }
*/
       /* for (int i = 0; i < geoCountryObjects.size(); i++) {
            GeoCountryObject geoCountryObject = geoCountryObjects.get(i);
            System.out.println("geoCountryObject.toKaf() = " + geoCountryObject.toKaf());
        }
        System.out.println("geoCountryObjects = " + geoCountryObjects.size());
        System.out.println("geoCountryObjects = " + geoCountryObjects.toString());
        System.out.println("geoPlaceObjects = " + geoPlaceObjects.size());
        System.out.println("geoPlaceObjects = " + geoPlaceObjects.toString());*/
    }

    public Element toXML(Document xmldoc)
    {
        Element root = xmldoc.createElement(name);

        if (mention != null)
            root.setAttribute("mention", mention);
        if (lemma != null)
            root.setAttribute("lemma", lemma);

        if ((concept != null) && !concept.isEmpty())   {
            root.setAttribute("concept", concept);
            root.setAttribute("confidence", new Double(confidence).toString());
        }

        if ((reference != null) && !reference.isEmpty())   {
            root.setAttribute("reference", reference);
        }

        if ((pos != null) && !pos.isEmpty())   {
            root.setAttribute("pos", pos);
        }

        if (tokens.size()>0) {
            String tokenString = "";
            for (int i = 0; i < tokens.size(); i++) {
                if (i>0) {
                    tokenString+=";";
                }
                String s = tokens.get(i);
                tokenString+=s;
            }
            root.setAttribute("tokens",tokenString);
        }
        if ((role != null) && !role.isEmpty())   {
            root.setAttribute("role", role);
        }
        if ((depRelation != null) && !depRelation.isEmpty())   {
            root.setAttribute("dep", depRelation);
        }

        return root;
    }

    public Element toXmlWithDatePlace(Document xmldoc)
    {
        Element root = xmldoc.createElement(name);

        if (mention != null)
            root.setAttribute("mention", mention);
        if (lemma != null)
            root.setAttribute("lemma", lemma);

        if ((concept != null) && !concept.isEmpty())   {
            root.setAttribute("concept", concept);
            root.setAttribute("confidence", new Double(confidence).toString());
        }

        if ((pos != null) && !pos.isEmpty())   {
            root.setAttribute("pos", pos);
        }
        if ((role != null) && !role.isEmpty())   {
            root.setAttribute("role", role);
        }
        if (tokens.size()>0) {
            String tokenString = "";
            for (int i = 0; i < tokens.size(); i++) {
                if (i>0) {
                    tokenString+=";";
                }
                String s = tokens.get(i);
                tokenString+=s;
            }
            root.setAttribute("tokens",tokenString);
        }
        if (geoCountryObjects.size()>0) {
          //  System.out.println("geoCountryObjects = " + geoCountryObjects.size());
            for (int i = 0; i < geoCountryObjects.size(); i++) {
                GeoCountryObject geoCountryObject = geoCountryObjects.get(i);
                Element geoCountryElement = geoCountryObject.toEventXML(xmldoc);
                root.appendChild(geoCountryElement);
            }
        }
        if (geoPlaceObjects.size()>0) {
            for (int i = 0; i < geoPlaceObjects.size(); i++) {
                GeoPlaceObject geoPlaceObject = geoPlaceObjects.get(i);
                Element geoPlaceElement = geoPlaceObject.toEventXML(xmldoc);
                root.appendChild(geoPlaceElement);
            }
        }
        if (isoDates.size()>0) {
            for (int i = 0; i < isoDates.size(); i++) {
                ISODate isoDate = isoDates.get(i);
                Element geoDateElement = isoDate.toEventXML(xmldoc);
                root.appendChild(geoDateElement);
            }
        }
        return root;
    }
}
