package org.yafox.muse.validate;

import org.yafox.muse.util.BeanUtil;

public class SingletonNode extends AbstractNode {

    public void validate(Object target) throws Exception {
        Object value = BeanUtil.get(target, name);
        for (Validator validator : validators) {
            if (value == null) {
                if (validator instanceof ForceValidator) {
                    validator.validate(target);
                }
            } else {
                validator.validate(value);
            }
        }
        
        if (children != null && value != null) {
            for (Node child : children) {
                child.validate(value);
            }
        }
    }
    
}
