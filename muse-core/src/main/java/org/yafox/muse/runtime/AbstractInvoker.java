package org.yafox.muse.runtime;

import java.lang.reflect.Method;

import org.yafox.muse.Invoker;

public abstract class AbstractInvoker implements Invoker {

    protected String id;
    
    protected Method method;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

}
