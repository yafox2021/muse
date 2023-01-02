package org.yafox.muse.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Attribute Description
 * @author LinXueQin
 *
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Ad {

    String value() default "";
    
    String description() default "";
}
