package org.yafox.muse;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface Pallet {

    Object getBean(String name) throws Exception;
    
    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws Exception;
    
    String getString(String resourceId) throws Exception;
    
}
