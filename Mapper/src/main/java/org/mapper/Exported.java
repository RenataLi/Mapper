package org.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exported annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Exported {
    /**
     * NullHandling.
     *
     * @return enum.
     */
    NullHandling nullHandling() default NullHandling.EXCLUDE;

    /**
     * If property police is unknown.
     *
     * @return enum.
     */
    UnknownPropertiesPolicy unknownPropertyPolicy() default UnknownPropertiesPolicy.FAIL;

}