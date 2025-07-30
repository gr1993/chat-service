package com.example.chat_mvc.monitoring;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class PerformanceMeasurementAspect {

    @Around("@annotation(com.example.chat_mvc.monitoring.MeasureExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTimeNanos = System.nanoTime();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        MeasureExecutionTime annotation = method.getAnnotation(MeasureExecutionTime.class);
        String metricName = Optional.ofNullable(annotation.value())
                .filter(s -> !s.isEmpty())
                .orElseGet(() -> method.getName() + "ExecutionTime");

        String requestContext = getRequestContext(joinPoint);

        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long endTimeNanos = System.nanoTime();
            long executionTimeNanos = endTimeNanos - startTimeNanos;
            long executionTimeMillis = TimeUnit.NANOSECONDS.toMillis(executionTimeNanos); // 밀리초 단위
            double executionTimeMicros = (double) executionTimeNanos / 1_000.0; // 마이크로초 단위 (소수점 포함)

            System.out.println(String.format(
                    "[%s] Method: %s.%s() 실행 시간: %.2f μs (%d ms). Context: %s",
                    metricName,
                    joinPoint.getTarget().getClass().getSimpleName(),
                    method.getName(),
                    executionTimeMicros,
                    executionTimeMillis,
                    requestContext
            ));
        }
    }

    private String getRequestContext(ProceedingJoinPoint joinPoint) {
        // 1. HTTP 요청 컨텍스트 확인
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return String.format("HTTP %s %s", request.getMethod(), request.getRequestURI());
        }
        return "Non-HTTP";
    }
}
