/**
 * 
 */
package org.yafox.muse.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Service
 * @author LinXueQin
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface Svc {

    String value();
    
}
