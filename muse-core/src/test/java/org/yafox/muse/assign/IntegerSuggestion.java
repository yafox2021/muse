package org.yafox.muse.assign;

import org.yafox.muse.assign.Suggestion;

public class IntegerSuggestion implements Suggestion {

    private Integer value;

    public Object suggest(Object oldValue) throws Exception {
        return value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}
