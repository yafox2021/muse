package org.yafox.muse.assign;

public class Assignment extends AbstractNode {

    public void assign(Object target) throws Exception {
        if (children != null) {
            for (Node child : children) {
                child.assign(target);
            }
        }
    }

}
