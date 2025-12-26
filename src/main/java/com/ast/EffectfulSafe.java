package com.ast;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@Target({METHOD, TYPE})
public @interface EffectfulSafe {

    //A value returned from an @EffectfulSafe method must be immutable or thread-confined

}
