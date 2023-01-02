package org.yafox.muse.validate.validators;

import java.lang.reflect.Array;
import java.util.Collection;

import org.yafox.muse.validate.ValidationException;

public class LengthValidator extends AbstractValidator {

    private Integer max;

    private Integer min;

    public void validate(Object target) throws Exception {
        if (max == null && min == null) {
            return;
        }

        int len = calcLength(target);

        if (max != null && len > max) {
            throw new ValidationException(message);
        }

        if (min != null && len < min) {
            throw new ValidationException(message);
        }
    }

    @SuppressWarnings("rawtypes")
    private int calcLength(Object target) throws Exception {
        if (target instanceof String) {
            return ((String) target).length();
        } else if (target.getClass().isArray()) {
            return Array.getLength(target);
        } else if (target instanceof Collection) {
            return ((Collection) target).size();
        } else {
            throw new Exception("can not use length validator on type " + target.getClass().getName());
        }
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

}
