package vu.kafkybot;

import eu.kyotoproject.kaf.KafSaxParser;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/3/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafResult {


   // private vu.kafkybot.TupleElement element;
    private ArrayList<TupleElement> children;
    private String profileName;
    private int profileConfidence;
    private String id;
    private String sentenceId;
    private String relation;
    private String comment;

    public KafResult() {
        this.profileName = "";
        this.profileConfidence = 0;
      //  this.element = new vu.kafkybot.TupleElement();
        this.id = "";
        this.relation = "";
        this.sentenceId = "";
        this.comment = "";
        children = new ArrayList<TupleElement>();
    }

    public KafResult(String name) {
        this.profileName = name;
      //  this.element = new vu.kafkybot.TupleElement();
        this.profileConfidence = 0;
        this.id = "";
        this.relation = "";
        this.sentenceId = "";
        this.comment = "";
        children = new ArrayList<TupleElement>();
    }

 /*   public vu.kafkybot.TupleElement getElement() {
        return element;
    }

    public void setElement(vu.kafkybot.TupleElement event) {
        this.element = event;
    }
*/

    public int getProfileConfidence() {
        return profileConfidence;
    }

    public void setProfileConfidence(int profileConfidence) {
        this.profileConfidence = profileConfidence;
    }

    public ArrayList<TupleElement> getChildren() {
        return children;
    }

    public void addChildren(TupleElement element) {
        this.children.add(element);
    }

    public String getComment() {
        return comment.trim();
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public void addComment(String comment) {
        this.comment += comment;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }


    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
    }

    public void setDateAndTime(KafSaxParser kafSaxParser, String elementName) {
        for (int i = 0; i < children.size(); i++) {
            TupleElement tupleElement = children.get(i);
            if (tupleElement.getName().toLowerCase().startsWith(elementName)) {
                tupleElement.setTimeAndPlace(kafSaxParser);
            }
        }
    }

    public boolean isEqual (KafResult kafResult) {
        if (!kafResult.getRelation().equals(relation)) {
            return false;
        }
        if (kafResult.getChildren().size()!=this.getChildren().size()) {
            return false;
        }
        for (int i = 0; i < children.size(); i++) {
            TupleElement tupleElement = children.get(i);
            boolean match = false;
            for (int j = 0; j < kafResult.getChildren().size(); j++) {
                TupleElement element = kafResult.getChildren().get(j);
                if (tupleElement.getName().equals(element.getName()) &&
                    element.getMention().equals(tupleElement.getMention())) {
                    //same name and same mention
                    match = true;
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    public Element toXML(Document xmldoc)
    {
        Element root = xmldoc.createElement("tuple");
        if (id != null)
            root.setAttribute("id", id);
        if ((profileName != null) && !profileName.isEmpty())
            root.setAttribute("profile", profileName);

        if (profileConfidence>0)
            root.setAttribute("profileConfidence", new Integer(profileConfidence).toString());

        if (sentenceId != null)
            root.setAttribute("sentenceId", sentenceId);

        if ((relation != null) && (!relation.isEmpty())) {
            root.setAttribute("relation", relation);
    }

        if ((comment != null) && !this.getComment().isEmpty())  {
            Comment comment = xmldoc.createComment(this.getComment());
            root.appendChild(comment);
        }

        for (int i = 0; i < children.size(); i++) {
            TupleElement tupleElement = children.get(i);
            if (tupleElement.getName().equalsIgnoreCase("event")) {
                Element participantXml = tupleElement.toXmlWithDatePlace(xmldoc);
                root.appendChild(participantXml);
            }
            else {
                Element participantXml = tupleElement.toXML(xmldoc);
                root.appendChild(participantXml);
            }
        }
        return root;
    }
}
