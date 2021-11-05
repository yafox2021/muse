package org.yafox.muse;

import java.lang.reflect.Method;

public interface ProxyHandler {

    Object handle(String name, String methodId, Method method, Object input, Class<?> responseType) throws Exception;
    
}
