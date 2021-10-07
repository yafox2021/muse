
package org.yafox.muse;

import org.yafox.muse.assign.Assignment;
import org.yafox.muse.validate.Validator;

public class MuseInvoker extends DelegateInvoker {

    private Assignment assignment;

    private Validator validator;

    private Assignment mask;

    @Override
    public Object invoke(Object input) throws Exception {
        if (assignment != null) {
            assignment.assign(input);
        }

        if (validator != null) {
            validator.validate(input);
        }

        Object result = super.invoke(input);

        if (mask != null) {
            mask.assign(result);
        }

        return result;

    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public Assignment getMask() {
        return mask;
    }

    public void setMask(Assignment mask) {
        this.mask = mask;
    }

}
