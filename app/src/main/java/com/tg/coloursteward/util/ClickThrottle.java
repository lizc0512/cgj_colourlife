package com.tg.coloursteward.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface ClickThrottle {
    /**
     * 点击间隔时间
     * @return
     */
    int value() default AspectUtils.DEFAULT_LIMIT;
}
