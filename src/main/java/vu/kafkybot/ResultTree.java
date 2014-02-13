package vu.kafkybot;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/10/13
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultTree {
    TupleElement element;
    ArrayList<ResultTree> children;

    public ResultTree() {
        this.children = new ArrayList<ResultTree>();
        this.element = new TupleElement();
    }

    public ArrayList<ResultTree> getChildren() {
        return children;
    }

    public void addChild(ResultTree child) {
        this.children.add(child);
    }

    public TupleElement getElement() {
        return element;
    }

    public void setElement(TupleElement element) {
        this.element = element;
    }

    ArrayList<ArrayList<TupleElement>> getTupleElements() {
        ArrayList<ArrayList<TupleElement>> elements = new ArrayList<ArrayList<TupleElement>>();
        if (children.size()>0) {
            for (int i = 0; i < children.size(); i++) {
                ResultTree child = children.get(i);
                ArrayList<ArrayList<TupleElement>> childResults = child.getTupleElements();
                for (int k = 0; k < childResults.size(); k++) {
                    ArrayList<TupleElement> tupleElements = childResults.get(k);
                    if (!element.getLemma().isEmpty()) {
                        tupleElements.add(element);
                    }
                    elements.add(tupleElements);
                    //System.out.println("elements = " + elements.size());
                }
            }
        }
        else {
            if (!element.getLemma().isEmpty()) {
                ArrayList<TupleElement> result = new ArrayList<TupleElement>();
                result.add(element);
                elements.add(result);
            }
        }
        return elements;
    }


    public String printTree (int lvl) {
        String str = "";
        String tab = "";
        for (int i = 0; i < lvl; i++) {
             tab+= " ";
        }
        str += tab+element.getLemma()+"\n";
        for (int i = 0; i < children.size(); i++) {
            ResultTree resultTree = children.get(i);
            str += resultTree.printTree(lvl+1);
        }
        return str;
    }

}
