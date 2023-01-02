package org.yafox.muse.validate.validators;

import org.yafox.muse.validate.ForceValidator;
import org.yafox.muse.validate.ValidationException;

public class RequiredValidator extends AbstractValidator implements ForceValidator {

    public void validate(Object target) throws Exception {
        if (target == null) {
            throw new ValidationException(message);
        }
    }

}
