package com.bnroll.annotations.ratelimit;

import com.bnroll.commercedomain.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;
    private final HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object handleRateLimit(
            ProceedingJoinPoint joinPoint,
            RateLimit rateLimit
    ) throws Throwable {

        String key = buildKey(joinPoint);

        if (!rateLimitService.allowRequest(
                key,
                rateLimit.limit(),
                rateLimit.durationSeconds())) {

            throw new AuthException(
                    "too.many.requests",
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }

        return joinPoint.proceed();
    }

    private String buildKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        return signature.getMethod().getName()
                + ":"
                + request.getRemoteAddr();
    }
}