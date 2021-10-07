package org.yafox.muse.assign;

import java.util.List;

public abstract class AbstractNode implements Node {

    protected String name;

    protected List<Node> children;

    protected Suggestion suggestion;

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

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }


}
