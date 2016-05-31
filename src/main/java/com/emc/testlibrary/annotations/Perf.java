package com.emc.testlibrary.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/11/16
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Perf {
    /**
     * Any message to be printed
     * @return
     */
    String message() default "";

    /**
     * Any comments to be printed
     * @return
     */
    String comments() default "";
}
