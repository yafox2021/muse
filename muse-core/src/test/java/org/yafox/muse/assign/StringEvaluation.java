package org.yafox.muse.assign;

import org.yafox.muse.Evaluation;

public class StringEvaluation implements Evaluation {

    private String value;

    public Object evaluate(Object oldValue) throws Exception {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
