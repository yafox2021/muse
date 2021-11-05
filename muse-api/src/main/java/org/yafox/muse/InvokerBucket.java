package org.yafox.muse;

public interface InvokerBucket {

    void addInvoker(String id, Invoker invoker);
    
    Invoker findInvoker(String id);
}
