package org.yafox.muse.validate;

import java.util.List;

import org.yafox.muse.Validator;

public abstract class AbstractNode implements Node {

    protected String name;

    protected List<Node> children;

    protected List<Validator> validators;

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

    public List<Validator> getValidators() {
        return validators;
    }

    public void setValidators(List<Validator> validators) {
        this.validators = validators;
    }

}
