package net.jetcobblestone.pluginmcu.util.annotationscheduling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TickableMethod {
    int ticks() default 1;
    int initialDelay() default 0;
}
