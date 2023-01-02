/**
 * 
 */
package org.yafox.muse;

import java.lang.reflect.Method;

public interface Assistant {

    /**
     * @param method
     * @return
     * @throws Exception
     */
    String[] parameterNames(Method method) throws Exception;
    

    /**
     * @param method
     * @return
     * @throws Exception
     */
    String[] parameterAlisNames(Method method) throws Exception;
    
    /**
     * @param method
     * @return
     * @throws Exception
     */
    String spi(Method method) throws Exception;

}
