package org.yafox.muse.assign;

import org.yafox.muse.Evaluation;

public class IntegerEvaluation implements Evaluation {

    private Integer value;

    public Object evaluate(Object oldValue) throws Exception {
        return value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}
