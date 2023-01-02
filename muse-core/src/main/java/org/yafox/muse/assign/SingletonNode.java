package org.yafox.muse.assign;

import org.yafox.muse.util.BeanUtil;

public class SingletonNode extends AbstractNode {

    public void assign(Object target) throws Exception {

        Object value = null;

        if ("".equals(name)) {// root node's name is empty string
            value = target;
        } else {
            value = BeanUtil.get(target, name);
        }

        Object newValue = null;

        if (evaluation != null) {
            newValue = evaluation.evaluate(value);
            BeanUtil.set(target, name, newValue);
        } else {
            newValue = value;
        }

        if (children != null && newValue != null) {
            for (Node child : children) {
                child.assign(newValue);
            }
        }
    }

}
