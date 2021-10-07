package org.yafox.muse.assign;

import org.yafox.muse.assign.Suggestion;

public class StringSuggestion implements Suggestion {

    private String value;

    public Object suggest(Object oldValue) throws Exception {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
