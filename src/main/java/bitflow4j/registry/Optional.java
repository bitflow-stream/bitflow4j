package bitflow4j.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {

    boolean defaultBool() default false;

    String defaultString() default "";

    int defaultInt() default 0;

    long defaultLong() default 0L;

    float defaultFloat() default 0F;

    double defaultDouble() default 0D;

}
