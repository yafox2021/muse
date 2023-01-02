package org.yafox.muse.validate.validators;

import java.util.List;

import org.yafox.muse.validate.ValidationException;

public class EnumStringValidator extends AbstractValidator {

    private List<String> items;

    public void validate(Object target) throws Exception {
        if (items.indexOf(target) == -1) {
            throw new ValidationException(message);
        }
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

}
