package org.yafox.muse.validate;

import org.yafox.muse.Validator;
import org.yafox.muse.util.BeanUtil;

public class SingletonNode extends AbstractNode {

    public void validate(Object target) throws Exception {
        Object value = null;
        if ("".equals(name)) { // root node's name is empty string
            value = target;
        } else {
            value = BeanUtil.get(target, name);
        }
        
        if (validators != null) {
            for (Validator validator : validators) {
                if (value == null) {
                    if (validator instanceof ForceValidator) {
                        validator.validate(value);
                    }
                } else {
                    validator.validate(value);
                }
            }
        }
        
        if (children != null && value != null) {
            for (Node child : children) {
                child.validate(value);
            }
        }
    }
    
}
