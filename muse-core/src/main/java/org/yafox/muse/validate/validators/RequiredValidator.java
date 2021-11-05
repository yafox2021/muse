package org.yafox.muse.validate.validators;

import javax.xml.bind.ValidationException;

public class RequiredValidator extends AbstractValidator {

    public void validate(Object target) throws Exception {
        if (target == null) {
            throw new ValidationException(message);
        }
    }

}
