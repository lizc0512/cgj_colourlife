package com.tg.coloursteward.util;

//import cn.ninebot.commonlibs.log.LogManager;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


//@Aspect
public class AspectUtils {
    String TAG = "AspectUtils";

    private static long lastClickTime;//上次点击时的系统时间
    public final static int DEFAULT_LIMIT = 500;

    /**
     * 判断两次点击是否过快的方法，限制时间间隔为2000ms
     *
     * @param limitTime 用户可以动态设置限制时间，单位毫秒
     * @return true 点击过快，false点击未过快
     */
    private boolean isTooFastClick(long limitTime) {
        boolean result = true;
        long currentTimeMillis = getCurrentSystemTime();
        if (currentTimeMillis - lastClickTime >= limitTime) {
            result = false;
        }
        lastClickTime = currentTimeMillis;
        return result;
    }

    private boolean isTooFastClick() {
        return isTooFastClick(DEFAULT_LIMIT);
    }

    /**
     * 获取系统当前时间
     *
     * @return 当前系统时间的毫秒值
     */
    private static long getCurrentSystemTime() {
        return System.currentTimeMillis();
    }

//    @Around("execution(@ClickThrottle * *(..))")
//    public void clickThrottle(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        // 取出方法的注解
//        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//        if (!method.isAnnotationPresent(ClickThrottle.class)) {
//            return;
//        }
//        ClickThrottle clickThrottle = method.getAnnotation(ClickThrottle.class);
//        if (!isTooFastClick(clickThrottle.value())) {
//            proceedingJoinPoint.proceed();
////            LogManager.d(TAG, "proceed");
//        }
//        else {
//            LogManager.d(TAG, "isTooFast");
//        }
//    }
}
