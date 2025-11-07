package com.mar.libhome.db.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
public class MethodLoggerAspect {

    private final Logger log = LoggerFactory.getLogger(MethodLoggerAspect.class);

    @Around("@annotation(ApiLog)")
    public Object apiLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature s = joinPoint.getSignature();
        log.debug(">> Send API method: {}, arg: {}", s.getName(), joinPoint.getArgs());
        Object rez = joinPoint.proceed();
        if (rez instanceof Collection) {
            log.debug("<< End API method: {}, result count collection: {}", s.getName(), ((Collection<?>) rez).size());
        } else {
            log.debug("<< End API method: {}, result: {}", s.getName(), rez);
        }
        return rez;
    }

}
