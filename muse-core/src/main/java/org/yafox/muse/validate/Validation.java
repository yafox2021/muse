package org.yafox.muse.validate;

public class Validation extends AbstractNode {

    public void validate(Object target) throws Exception {
        if (validators != null) {
            for (Validator validator : validators) {
                if (target == null) {
                    if (validator instanceof ForceValidator) {
                        validator.validate(target);
                    }
                } else {
                    validator.validate(target);
                }
            }
        }

        if (children != null && target != null) {
            for (Node child : children) {
                child.validate(target);
            }
        }

    }

}
