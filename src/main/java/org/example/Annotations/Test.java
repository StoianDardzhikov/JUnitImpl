package org.example.Annotations;

import org.example.NullException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class<? extends Throwable> expected() default NullException.class;

    long timeout() default 0L;

    String[] dependsOnMethods() default {};
}