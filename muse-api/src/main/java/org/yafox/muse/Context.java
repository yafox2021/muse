package org.yafox.muse;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private static final ThreadLocal<Map<String, Object>> CONTEXT_THREADLOCAL = new ThreadLocal<Map<String, Object>>();

    public static void set(String name, Object value) {
        Map<String, Object> map = CONTEXT_THREADLOCAL.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            CONTEXT_THREADLOCAL.set(map);
        }
        map.put(name, value);
    }

    public static Object get(String name) {
        Map<String, Object> map = CONTEXT_THREADLOCAL.get();
        if (map != null) {
            return map.get(name);
        }
        return null;
    }
    
    public static void clear() {
        CONTEXT_THREADLOCAL.set(null);
    }

}
