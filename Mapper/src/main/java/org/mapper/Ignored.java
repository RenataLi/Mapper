package org.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ignored annotation.
 */
@Target({
        ElementType.FIELD,
        ElementType.RECORD_COMPONENT
})
/**
 * Retention.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignored {
}