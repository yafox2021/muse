package org.yafox.muse.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Function
 * @author LinXueQin
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Fn {

    String value() default "";
    
    String description() default "";
}
