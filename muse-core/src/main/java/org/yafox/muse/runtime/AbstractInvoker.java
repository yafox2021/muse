package org.yafox.muse.runtime;

import org.yafox.muse.Invoker;

public abstract class AbstractInvoker implements Invoker {

    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
