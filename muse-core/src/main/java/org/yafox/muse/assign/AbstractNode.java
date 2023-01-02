package org.yafox.muse.assign;

import java.util.List;

import org.yafox.muse.Evaluation;

public abstract class AbstractNode implements Node {

    protected String name;

    protected List<Node> children;

    protected Evaluation evaluation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

}
