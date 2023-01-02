
package org.yafox.muse.runtime;

import org.yafox.muse.DelegateInvoker;
import org.yafox.muse.assign.Assignment;
import org.yafox.muse.validate.Validation;

public class MuseInvoker extends DelegateInvoker {

    private Assignment assignment;

    private Validation validation;

    private Assignment mask;

    @Override
    public Object invoke(Object input) throws Exception {
        if (assignment != null) {
            assignment.assign(input);
        }

        if (validation != null) {
            validation.validate(input);
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

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public Assignment getMask() {
        return mask;
    }

    public void setMask(Assignment mask) {
        this.mask = mask;
    }

}
