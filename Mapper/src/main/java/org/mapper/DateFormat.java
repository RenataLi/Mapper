package org.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation of Date foemat.
 */
@Target({
        ElementType.FIELD,
        ElementType.RECORD_COMPONENT
})
/**
 * Retention.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {
    /**
     * Value of element.
     *
     * @return string.
     */
    String value();
}

