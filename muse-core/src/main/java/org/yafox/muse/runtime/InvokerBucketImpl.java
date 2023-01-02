package org.yafox.muse.runtime;

import java.util.HashMap;
import java.util.Map;

import org.yafox.muse.Invoker;
import org.yafox.muse.InvokerBucket;

public class InvokerBucketImpl implements InvokerBucket {

    private Map<String, Invoker> map = new HashMap<String, Invoker>();
    
    public void addInvoker(String id, Invoker invoker) {
        map.put(id, invoker);
    }

    public Invoker findInvoker(String id) {
        return map.get(id);
    }

}
