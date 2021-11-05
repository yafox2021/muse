package org.yafox.muse.validate.validators;

import org.yafox.muse.validate.Validator;

public abstract class AbstractValidator implements Validator {

    protected String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
