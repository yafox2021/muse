package org.yafox.muse;

public interface Invoker {

    String getId();
    
    Class<?> getRequestType();
    
    Class<?> getResponseType();
    
    void setTarget(Object target);
    
    Object invoke(Object input) throws Exception;
    
}
