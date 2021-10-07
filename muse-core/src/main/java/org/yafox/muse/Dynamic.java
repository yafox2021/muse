package org.yafox.muse;

import java.lang.reflect.Method;

public interface Dynamic {

    Class<?> getProxyType(Class<?> interfaceType) throws Exception;
    
    Class<?> getInvokerType(Method method) throws Exception;
    
}
